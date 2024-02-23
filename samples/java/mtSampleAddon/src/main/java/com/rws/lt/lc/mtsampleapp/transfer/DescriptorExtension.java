package com.rws.lt.lc.mtsampleapp.transfer;

import lombok.Getter;

import java.util.Map;

@Getter
public class DescriptorExtension {

    private String id;
    private String name;
    private String extensionPointVersion;
    private String extensionPointId;
    private String description;
    private Map<String, Object> configuration;
}
