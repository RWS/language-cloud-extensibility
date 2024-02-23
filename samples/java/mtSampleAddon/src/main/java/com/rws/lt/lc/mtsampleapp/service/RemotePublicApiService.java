package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.mtsampleapp.exception.RemoteServiceException;
import com.rws.lt.lc.mtsampleapp.security.JWSExtractor;
import com.rws.lt.lc.mtsampleapp.security.dto.JWK;
import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Slf4j
public class RemotePublicApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${integration.public-api.retrievePublicKeyByIdUrl}")
    private String retrievePublicKeysUrl;

    public JWK getPublicKeyById(String kid) {
        LOGGER.info("Retrieving public key with kid {} from public Api.", kid);
        String expandedUrl = UriComponentsBuilder.fromUriString(retrievePublicKeysUrl)
                .buildAndExpand(Map.of("kid", kid))
                .toUriString();
        try {
            return restTemplate.getForObject(expandedUrl, JWK.class);
        } catch (RemoteServiceException e) {
            if (ErrorResponse.NOT_FOUND_ERROR_CODE.equals(e.getErrorResponse().getErrorCode())) {
                LOGGER.error("JWS key '{}' not found. Invalid key id?", kid);
                throw new JWSExtractor.IllegalJWSException(String.format("JWT key '%s' not found.", kid));
            }
            throw e;
        }
    }
}