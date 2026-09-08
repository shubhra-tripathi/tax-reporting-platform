package com.demo.taxreporting.generator;

import com.demo.taxreporting.mapper.PostingMapper;
import com.demo.taxreporting.output.PostingCsvWriter;
import com.demo.taxreporting.output.RejectedPostingCsvWriter;
import com.demo.taxreporting.source.KaggleTransactionCsvReader;
import com.demo.taxreporting.validation.PostingValidator;


import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public class PostingDataGenerator {

    private final KaggleTransactionCsvReader reader;
    private final PostingMapper mapper;
    private final PostingValidator validator;


    public PostingDataGenerator(KaggleTransactionCsvReader reader, PostingMapper mapper, PostingValidator validator) {
        this.reader = reader;
        this.mapper = mapper;
        this.validator = validator;
    }

    public GenerationStatistics generate(Path input, Path validOutput, Path rejectedOutput) throws Exception {
        AtomicLong inputCount = new AtomicLong();
        AtomicLong validCount = new AtomicLong();
        AtomicLong rejectedCount = new AtomicLong();

        try (var transactions = reader.read(input);
             var validWriter = new PostingCsvWriter(validOutput);
             var rejectedWriter = new RejectedPostingCsvWriter(rejectedOutput)) {

            transactions.forEach(source -> {
                inputCount.incrementAndGet();
                var posting = mapper.map(source);

                var validation = validator.validate(posting);

                try{
                    if(validation.valid()) {
                        validWriter.write(posting);
                        validCount.incrementAndGet();
                    }else {
                        rejectedWriter.write(posting, validation);
                        rejectedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to write posting " + posting.postingId(), e);
                }
            });
        }

        return new GenerationStatistics(
                inputCount.get(),
                validCount.get(),
                rejectedCount.get()
        );
    }
}
