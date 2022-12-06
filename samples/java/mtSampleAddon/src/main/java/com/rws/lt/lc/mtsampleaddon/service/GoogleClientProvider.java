package com.rws.lt.lc.mtsampleaddon.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.translate.TranslateScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;
import com.rws.lt.lc.mtsampleaddon.domain.AccountSettings;
import com.rws.lt.lc.mtsampleaddon.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorDetail;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleClientProvider {

    /**
     * Creates a translation service client with the provided account settings.
     * @param settings the account settings
     * @throws InvalidConfigurationException when the settings are invalid
     */
    public TranslationServiceClient getTranslationServiceClient(AccountSettings settings) throws InvalidConfigurationException {
        validate(settings);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(settings.getServiceAccountKey().getBytes())) {
            GoogleCredentials.Builder credentialBuilder = GoogleCredentials.fromStream(stream).createScoped(TranslateScopes.CLOUD_TRANSLATION).toBuilder();

            TranslationServiceSettings translationServiceSettings = TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentialBuilder.build()))
                    .build();

            return TranslationServiceClient.create(translationServiceSettings);
        } catch (IOException e) {
            throw new RuntimeException(ErrorResponse.INTERNAL_SERVER_ERROR_MESSAGE, e);
        }
    }

    /**
     * Validates the account settings.
     * @param settings the account settings
     * @throws InvalidConfigurationException containing some details about the missing settings
     */
    public void validate(AccountSettings settings) throws InvalidConfigurationException {
        List<String> missing = new ArrayList<>();
        if (StringUtils.isBlank(settings.getGoogleProjectId())) {
            missing.add(AccountSettings.SAMPLE_PROJECT_ID);
        }
        if (StringUtils.isBlank(settings.getServiceAccountKey())) {
            missing.add(AccountSettings.SAMPLE_ACCOUNT_SECRET);
        }
        if (!CollectionUtils.isEmpty(missing)) {
            InvalidConfigurationException exception = new InvalidConfigurationException(ErrorResponse.NOT_CONFIGURED_MESSAGE);
            for (String missingConfig : missing) {
                exception.addError(new ErrorDetail(missingConfig, ErrorResponse.NULL_VALUE, null));
            }
            throw exception;
        }
    }

}
