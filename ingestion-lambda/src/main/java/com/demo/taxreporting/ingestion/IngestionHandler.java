package com.demo.taxreporting.ingestion;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.sfn.SfnClient;

import java.util.UUID;

public class IngestionHandler
        implements RequestHandler<S3Event, IngestionResult> {

    private static final String STATE_MACHINE_ARN =
            System.getenv("STATE_MACHINE_ARN");

    private final FeedTypeResolver resolver;
    private final ObjectMapper objectMapper;
    private final WorkflowStarter workflowStarter;

    public IngestionHandler() {
        this(
                new FeedTypeResolver(),
                new ObjectMapper(),
                new WorkflowStarter(
                        SfnClient.create(),
                        STATE_MACHINE_ARN
                )
        );
    }

    IngestionHandler(
            FeedTypeResolver resolver,
            ObjectMapper objectMapper,
            WorkflowStarter workflowStarter) {

        this.resolver = resolver;
        this.objectMapper = objectMapper;
        this.workflowStarter = workflowStarter;
    }

    @Override
    public IngestionResult handleRequest(
            S3Event event,
            Context context) {

        if (event == null
                || event.getRecords() == null
                || event.getRecords().isEmpty()) {

            throw new IllegalArgumentException(
                    "S3 event must contain at least one record"
            );
        }

        var logger = context.getLogger();

        var record =
                event.getRecords().getFirst();

        String bucket =
                record.getS3()
                        .getBucket()
                        .getName();

        String key =
                record.getS3()
                        .getObject()
                        .getKey();

        String runId =
                UUID.randomUUID().toString();

        FeedType feedType =
                resolver.resolve(key);

        String status =
                feedType == FeedType.UNKNOWN
                        ? "REJECTED"
                        : "ACCEPTED";

        logger.log(
                "runId=" + runId
                        + ", bucket=" + bucket
                        + ", key=" + key
                        + ", feedType=" + feedType
                        + ", status=" + status
        );

        IngestionResult result =
                new IngestionResult(
                        runId,
                        bucket,
                        key,
                        feedType,
                        status
                );

        try {

            String workflowInput =
                    objectMapper.writeValueAsString(result);

            workflowStarter.start(workflowInput);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to start Step Functions workflow",
                    e
            );
        }

        return result;
    }
}