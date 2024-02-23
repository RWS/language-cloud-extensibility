package com.rws.lt.lc.blueprint.transfer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AutomaticTaskSubmitRequest {
    private String projectId;
    private String taskId;
    private String callbackUrl;
    private List<WorkflowConfiguration> workflowConfiguration;
}
