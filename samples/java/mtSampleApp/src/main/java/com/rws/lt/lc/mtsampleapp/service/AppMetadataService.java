package com.rws.lt.lc.mtsampleapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppMetadataService implements InitializingBean {

    @Value("${baseUrl}")
    private String baseUrl;

    @Getter
    private final ObjectNode descriptor;

    private List<String> secretConfigs;

    @Override
    public void afterPropertiesSet() {
        descriptor.put("baseUrl", baseUrl);
    }

    @Autowired
    public AppMetadataService() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        descriptor = objectMapper.readValue(getClass().getResourceAsStream("/descriptor.json"), ObjectNode.class);
    }

    public List<String> getSecretConfigurations() {
        if (secretConfigs == null) {
            List<String> computedSecretConfigs = new ArrayList<>();
            for (var configuration : descriptor.get("configurations")) {
                if ("secret".equals(configuration.get("dataType").asText())) {
                    computedSecretConfigs.add((configuration).get("id").asText());
                }
            }
            this.secretConfigs = computedSecretConfigs;
        }

        return this.secretConfigs;
    }
}
