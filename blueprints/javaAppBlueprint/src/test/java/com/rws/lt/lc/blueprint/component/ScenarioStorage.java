package com.rws.lt.lc.blueprint.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rws.lt.lc.blueprint.transfer.ErrorResponse;
import lombok.Getter;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ScenarioStorage {

    private static final ThreadLocal<ScenarioStorage> storage = new ThreadLocal<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    private Object lastResponse;
    private ErrorResponse lastErrorResponse;
    private int lastStatusCode;
    private final Map<String, Object> cache = new HashMap<>();

    public static void setLastResponse(ResponseEntity<?> response) {
        storage.get().setLastResponseEntity(response);
    }

    public static <T> T getLastResponse() {
        return (T) storage.get().lastResponse;
    }

    public static ErrorResponse getLastError() {
        return storage.get().lastErrorResponse;
    }

    public static int getLastStatusCode() {
        return storage.get().lastStatusCode;
    }

    public static void addToCache(String key, Object value) {
        storage.get().cache.put(key, value);
    }

    public static <T> T getFromCache(String key) {
        return (T) storage.get().cache.get(key);
    }

    public static void newStorage() {
        storage.set(new ScenarioStorage());
    }

    public static void reset() {
        storage.remove();
    }

    public void setLastResponseEntity(ResponseEntity<?> response) {
        lastStatusCode = response.getStatusCodeValue();
        if (lastStatusCode >= 400) {
            try {
                lastErrorResponse = mapper.readValue(mapper.writeValueAsString(response.getBody()), ErrorResponse.class);
            } catch (IOException e) {
                Assert.fail("Can't convert an error response to an ErrorResponse class");
            }
            lastResponse = null;
        } else {
            lastErrorResponse = null;
            lastResponse = response.getBody();
        }

    }
}
