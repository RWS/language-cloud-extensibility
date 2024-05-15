package com.rws.lt.lc.blueprint.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowConfiguration {
    private String id;
    private Object value;

    @Override
    public String toString() {
        return "WorkflowConfiguration{" +
                "id='" + id + '\'' +
                ", value=" + value +
                '}';
    }
}
