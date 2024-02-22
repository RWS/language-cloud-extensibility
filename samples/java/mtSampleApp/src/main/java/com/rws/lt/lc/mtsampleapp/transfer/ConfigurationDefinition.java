package com.rws.lt.lc.mtsampleapp.transfer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConfigurationDefinition implements Serializable {
    private String name;
    private String id;
    private String description;
    private boolean optional;
    private String dataType;
    private Object defaultValue;
    private List<Object> options;
}
