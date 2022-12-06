package com.rws.lt.lc.mtsampleaddon.service;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.translate.v3.*;
import com.rws.lt.lc.mtsampleaddon.domain.AccountSettings;
import com.rws.lt.lc.mtsampleaddon.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleaddon.persistence.AccountSettingsRepository;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorDetail;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class GoogleSupportedLanguagesService {

    private final AccountSettingsRepository accountSettingsRepository;
    private final GoogleClientProvider clientProvider;

    public GoogleSupportedLanguagesService(AccountSettingsRepository accountSettingsRepository, GoogleClientProvider clientProvider) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.clientProvider = clientProvider;
    }

    /**
     * Maps the languages retrieved from Google to an internal object {@link GoogleSupportedLanguages}
     */
    // consider caching this method for improved performance
    public GoogleSupportedLanguages getSupportedLanguages(String accountId, String model) throws InvalidConfigurationException {
        LOGGER.info("populateGoogleSupportedLanguages >> accountId {} and model {}", accountId, model);
        List<SupportedLanguage> supportedLanguages = fetchSupportedLanguages(accountId, model);

        Set<String> sources = new HashSet<>();
        Set<String> targets = new HashSet<>();

        for (SupportedLanguage language : supportedLanguages) {
            String languageCode = language.getLanguageCode();
            if (language.getSupportSource()) {
                sources.add(languageCode);
            }
            if (language.getSupportTarget()) {
                targets.add(languageCode);
            }
        }

        LOGGER.debug("Account {} and model {} has {} source languages and {} targetLanguages", accountId, model, sources.size(), targets.size());

        return new GoogleSupportedLanguages(sources, targets);
    }

    /**
     * Validates the account settings(credentials) by getting the supported languages from Google but with exception handling
     * @param accountId the accountId from the context
     * @param settings the account settings
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    public void validateAccountSettings(String accountId, AccountSettings settings) throws InvalidConfigurationException {
        LOGGER.debug("validateAccountSettings >> accountId {}", accountId);
        // instantiate a new translation service client with the account settings
        try (TranslationServiceClient client = clientProvider.getTranslationServiceClient(settings)) {
            // prepare a simple request
            LocationName parent = LocationName.of(settings.getGoogleProjectId(), settings.getLocation());
            GetSupportedLanguagesRequest request = GetSupportedLanguagesRequest.newBuilder().setParent(parent.toString()).build();

            // perform the request
            client.getSupportedLanguages(request);
        } catch (ApiException ex) {
            // handle the ApiException and generate a validation exception with details about the invalid fields(settings)
            // we only check the project id and location since the getTranslationServiceClient would throw an exception if the service account key is invalid
            InvalidConfigurationException exception = new InvalidConfigurationException(ex.getMessage());
            if (StringUtils.isNotBlank(settings.getPlainLocation())) {
                exception.addError(new ErrorDetail(AccountSettings.SAMPLE_LOCATION, ErrorResponse.INVALID_KEY, settings.getPlainLocation()));
            }
            exception.addError(new ErrorDetail(AccountSettings.SAMPLE_PROJECT_ID, ErrorResponse.INVALID_KEY, settings.getGoogleProjectId()));
            throw exception;
        }
    }

    /**
     * Fetches the supported languages from Google
     * @param accountId the accountId from the context
     * @param model the requested model
     * @return a list of supported languages
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    private List<SupportedLanguage> fetchSupportedLanguages(String accountId, String model) throws InvalidConfigurationException {
        LOGGER.debug("fetchSupportedLanguages >> accountId {} model {}", accountId, model);
        // get the stored account settings
        AccountSettings settings = accountSettingsRepository.findAccountSettings(accountId);
        // instantiate a new translation service client with the account settings
        try (TranslationServiceClient client = clientProvider.getTranslationServiceClient(settings)) {
            // prepare the request
            // https://cloud.google.com/translate/docs/advanced/discovering-supported-languages-v3
            LocationName parent = LocationName.of(settings.getGoogleProjectId(), settings.getLocation());
            GetSupportedLanguagesRequest.Builder builder = GetSupportedLanguagesRequest.newBuilder().setParent(parent.toString());
            GoogleHtmlUtil.toGoogleModel(model, settings).ifPresent(builder::setModel);
            GetSupportedLanguagesRequest request = builder.build();

            // perform the request
            SupportedLanguages supportedLanguages = client.getSupportedLanguages(request);

            return supportedLanguages.getLanguagesList();
        }
    }

}
