package com.demo.taxreporting.integration;


import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
class S3IntegrationTest {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:3.8.1");

    @Container
    static final LocalStackContainer localStack = new LocalStackContainer(LOCALSTACK_IMAGE).withServices(S3);

    @Test
    void shouldUploadAndReadFilesFromS3() {
        try (S3Client s3Client = createS3Client()) {

            String bucketName = "tax-reporting-test";
            String objectKey = "raw/posting/posting.csv";

            String csv = """
                    postingId,accountId,incomeType,amount
                    P001,A001,INTEREST,500
                    """;

            s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                    RequestBody.fromString(csv, StandardCharsets.UTF_8)
            );

            String result = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build()
            ).asUtf8String();

            assertEquals(csv, result);

        }
    }

    private S3Client createS3Client() {

        URI endPoint =  localStack.getEndpointOverride(S3);

        return S3Client.builder()
                .endpointOverride(endPoint)
                .region(Region.of(localStack.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStack.getAccessKey(),
                                        localStack.getSecretKey()
                                )
                        )
                ).forcePathStyle(true)
                .build();
    }

}
