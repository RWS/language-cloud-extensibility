package com.rws.lt.lc.mtsampleapp.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Descriptor {

    private String name;
    private String version;
    private String description;
    private String baseUrl;
    private String releaseNotes;
    private String minimumVersion;
    private DescriptorExtension[] extensions;
    private Map<String, String> standardEndpoints;
    private DescriptorConfiguration[] configurations;
    private DescriptorVendor vendor;
    private List<String> scopes;
    private String descriptorVersion;

}
