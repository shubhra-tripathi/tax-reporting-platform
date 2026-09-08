package com.demo.taxreporting.integration;


import com.demo.taxreporting.storage.S3PostingRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
public class S3PostingRepositoryIT {

    private static final String BUCKET_NAME = "tax-reporting-test";

    @Container
    static final LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.8.1")
    ).withServices(S3);

    static S3Client s3Client;

    static S3PostingRepository repository;

    @BeforeAll
    static void setUp() {

        s3Client = S3Client.builder()
                .endpointOverride(
                        localStack.getEndpointOverride(S3))
                .region(
                        Region.of(localStack.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStack.getAccessKey(),
                                        localStack.getSecretKey())))
                .forcePathStyle(true)
                .build();

        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(BUCKET_NAME)
                        .build());

        repository = new S3PostingRepository(s3Client);
    }

    @Test
    void shouldUploadAndReadPostingFile() {

        String objectKey = "raw/posting/posting.csv";

        String content =
                """
                postingId,accountId,incomeType,amount
                P001,A001,INTEREST,500
                """;

        repository.upload(
                BUCKET_NAME,
                objectKey,
                content
        );

        String actual = repository.read(
                BUCKET_NAME,
                objectKey
        );

        assertEquals(content, actual);
    }
}
