package com.demo.taxreporting.storage;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

public class S3PostingRepository {

    private final S3Client s3Client;

    public S3PostingRepository(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String bucketName, String objectKey, String content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromString(
                        content,
                        StandardCharsets.UTF_8
                )
        );
    }

    public String read(String bucketName, String objectKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        ResponseBytes<GetObjectResponse> response = S3Client.create().getObjectAsBytes(request);

        return response.asUtf8String();
    }
}
