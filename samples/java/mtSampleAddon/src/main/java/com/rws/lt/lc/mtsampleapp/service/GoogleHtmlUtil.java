package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.mtsampleapp.domain.AccountSettings;

import java.util.Optional;

public class GoogleHtmlUtil {

    private static final String MODEL_PATH_PATTERN = "projects/%s/locations/%s/models/general/%s";

    /**
     * Prepare the model path for the Google request
     * @param model the engine model
     * @param settings the account settings
     * @return the model path generated from the provided params
     */
    public static Optional<String> toGoogleModel(String model, AccountSettings settings) {
        return Optional.ofNullable(model)
                .map(m -> String.format(MODEL_PATH_PATTERN, settings.getGoogleProjectId(), settings.getLocation(), m));
    }
}
