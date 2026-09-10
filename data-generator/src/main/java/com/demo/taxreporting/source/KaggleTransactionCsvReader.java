package com.demo.taxreporting.source;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class KaggleTransactionCsvReader {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private OffsetDateTime parseTimestamp(String timestamp) {

        LocalDateTime localDateTime =
                LocalDateTime.parse(
                        timestamp,
                        TIMESTAMP_FORMATTER
                );

        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    public Stream<KaggleTransaction> read(Path path) throws IOException {

        Reader reader = Files.newBufferedReader(path);

        CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader().setSkipHeaderRecord(true)
                .build().parse(reader);

        return parser.stream()
                .map(this::mapRecord)
                .onClose(() -> close(parser));
    }

    private KaggleTransaction mapRecord(CSVRecord record) {

        return new KaggleTransaction(
                Long.parseLong(
                        record.get("transaction_id")),
                Long.parseLong(
                        record.get("customer_id")),
                parseTimestamp(record.get("transaction_timestamp")),
                new BigDecimal(
                        record.get("amount")),
                record.get("merchant_category")
        );
    }

    private void close(CSVParser parser) {
        try{
            parser.close();
        } catch(IOException e) {
            throw new IllegalStateException(
                    "Failed to close CSV parser", e);
        }
    }

}
