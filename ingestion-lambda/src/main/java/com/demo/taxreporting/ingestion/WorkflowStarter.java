package com.demo.taxreporting.ingestion;

import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

public class WorkflowStarter {

    private final SfnClient sfnClient;
    private final String stateMachineArn;


    public WorkflowStarter(SfnClient sfnClient, String stateMachineArn) {
        this.sfnClient = sfnClient;
        this.stateMachineArn = stateMachineArn;
    }

    public void start(String input) {
        StartExecutionRequest request = StartExecutionRequest.builder()
                .stateMachineArn(stateMachineArn)
                .input(input)
                .build();

        sfnClient.startExecution(request);
    }
}
