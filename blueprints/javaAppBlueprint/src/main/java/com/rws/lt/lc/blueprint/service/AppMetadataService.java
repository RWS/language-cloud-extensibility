package com.rws.lt.lc.blueprint.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppMetadataService implements InitializingBean {

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${multiRegion.enabled:false}")
    private boolean isMultiRegionEnabled;

    @Getter
    private final ObjectNode descriptor;

    private List<String> secretConfigs;

    private final ObjectMapper objectMapper;

    private final Environment env;

    @Override
    public void afterPropertiesSet() {
        descriptor.put("baseUrl", baseUrl);

        if (isMultiRegionEnabled) {
            ObjectNode regionalBaseUrls = objectMapper.createObjectNode();
            String euBaseUrl = env.getProperty("multiRegion.regionalBaseUrls.eu");
            String caBaseUrl = env.getProperty("multiRegion.regionalBaseUrls.ca");

            if(StringUtils.isNotEmpty(euBaseUrl)) {
                regionalBaseUrls.put("eu", euBaseUrl);
            }
            if(StringUtils.isNotEmpty(caBaseUrl)) {
                regionalBaseUrls.put("ca", caBaseUrl);
            }

            descriptor.put("regionalBaseUrls", regionalBaseUrls);
        }
    }

    @Autowired
    public AppMetadataService(Environment environment) throws IOException {
        env = environment;
        objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
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
