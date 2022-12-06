package com.rws.lt.lc.mtsampleaddon.transfer;

import lombok.Getter;

@Getter
public class DescriptorConfiguration {
    private String name;
    private String id;
    private String description;
    private boolean optional;
    private Object defaultValue;
    private String dataType;
}
