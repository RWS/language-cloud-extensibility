package com.rws.lt.lc.mtsampleapp.component.steps;

import com.rws.lt.lc.mtsampleapp.component.ScenarioStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class TestStepsBase {

    @Value("${local.server.port}")
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    public TestRestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getBaseUrl() {
        return "http://localhost:" + this.port;
    }

    public <T, K> ResponseEntity<K> exchange(String url, T body, HttpMethod method, Class<K> responseType) {
        ResponseEntity<K> response = getRestTemplate().exchange(getBaseUrl() + url, method, new HttpEntity<>(body), responseType);
        ScenarioStorage.setLastResponse(response);
        return response;
    }

}

