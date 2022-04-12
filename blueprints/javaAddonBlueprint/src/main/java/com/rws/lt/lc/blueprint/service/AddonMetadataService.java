package com.rws.lt.lc.blueprint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rws.lt.lc.blueprint.transfer.Descriptor;
import com.rws.lt.lc.blueprint.transfer.DescriptorConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AddonMetadataService implements InitializingBean {

    @Value("${baseUrl}")
    private String baseUrl;

    private final Descriptor descriptor;

    private List<String> secretConfigs;

    @Override
    public void afterPropertiesSet() {
        descriptor.setBaseUrl(baseUrl);
    }

    @Autowired
    public AddonMetadataService() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        descriptor = objectMapper.readValue(getClass().getResourceAsStream("/descriptor.json"), Descriptor.class);
    }

    public List<String> getSecretConfigurations() {
        if (secretConfigs == null) {
            List<String> computedSecretConfigs = new ArrayList<>();
            for (DescriptorConfiguration configuration : descriptor.getConfigurations()) {
                if ("secret".equals(configuration.getDataType())) {
                    computedSecretConfigs.add(configuration.getId());
                }
            }
            this.secretConfigs = computedSecretConfigs;
        }

        return this.secretConfigs;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }
}
