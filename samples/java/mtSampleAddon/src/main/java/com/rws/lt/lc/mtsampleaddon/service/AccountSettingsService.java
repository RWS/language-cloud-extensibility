package com.rws.lt.lc.mtsampleaddon.service;

import com.rws.lt.lc.mtsampleaddon.domain.AccountSettings;
import com.rws.lt.lc.mtsampleaddon.domain.ClientCredentials;
import com.rws.lt.lc.mtsampleaddon.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleaddon.exception.NotAuthorizedException;
import com.rws.lt.lc.mtsampleaddon.exception.ValidationException;
import com.rws.lt.lc.mtsampleaddon.persistence.AccountSettingsRepository;
import com.rws.lt.lc.mtsampleaddon.transfer.ConfigurationValue;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorDetail;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.ActivatedEvent;
import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.ActivatedEventDetails;
import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.AddonLifecycleEvent;
import com.rws.lt.lc.mtsampleaddon.transfer.lifecycle.DeactivatedEvent;
import com.rws.lt.lc.mtsampleaddon.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class AccountSettingsService {

    private static final String SECRET_VALUE = "***";

    private final AccountSettingsRepository accountSettingsRepository;
    private final AddonMetadataService metadataService;
    private final GoogleSupportedLanguagesService googleSupportedLanguagesService;

    @Autowired
    public AccountSettingsService(AccountSettingsRepository accountSettingsRepository, AddonMetadataService metadataService, GoogleSupportedLanguagesService googleSupportedLanguagesService) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.metadataService = metadataService;
        this.googleSupportedLanguagesService = googleSupportedLanguagesService;
    }

    public void handleAddonEvent(AddonLifecycleEvent lifecycleEvent) {
        LOGGER.debug("addonLifecycleEvent >> with type {} at {}", lifecycleEvent.getId(), lifecycleEvent.getTimestamp());
        if (lifecycleEvent instanceof ActivatedEvent) {
            handleAddonEvent((ActivatedEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof DeactivatedEvent) {
            handleAddonEvent((DeactivatedEvent) lifecycleEvent);
        }
    }

    public void handleAddonEvent(ActivatedEvent lifecycleEvent) {
        LOGGER.debug("handleAddonEvent >> for tenantId {} ", RequestLocalContext.getActiveAccountId());
        ActivatedEventDetails details = lifecycleEvent.getData();
        String tenantId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(tenantId);
        if (entity != null) {
            LOGGER.debug("Add-on already activated for tenantId {}", tenantId);
            return;
        }

        entity = new AccountSettings();
        entity.setAccountId(tenantId);
        if (details.getClientCredentials() != null) {
            entity.setClientCredentials(new ClientCredentials(details.getClientCredentials()));
        }

        accountSettingsRepository.save(entity);
    }

    public void handleAddonEvent(DeactivatedEvent lifecycleEvent) {
        String accountId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        if (entity != null) {
            accountSettingsRepository.delete(entity);
        }
    }

    public List<ConfigurationValue> saveOrUpdateConfigurations(String accountId, ConfigurationValue[] configurations) throws ValidationException, NotAuthorizedException {
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        if (entity == null) {
            throw new NotAuthorizedException("Account " + accountId + " is not activated");
        }

        entity.getConfigurations().putAll(getConfigurationValuesMap(configurations));

        // sensitive information such as the service account key(SAMPLE_ACCOUNT_SECRET) should be encrypted
        // https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/csfle/#mongodb-crypt
        accountSettingsRepository.save(entity);

        return getConfigurationsForExternalConsumption(accountId);
    }

    public void validateConfigurations(String accountId) throws InvalidConfigurationException {
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        // first check the settings exist
        if (entity == null) {
            throw new InvalidConfigurationException("Account " + accountId + " is not activated");
        }

        try {
            googleSupportedLanguagesService.validateAccountSettings(accountId, entity);
        } catch (InvalidConfigurationException ex) {
            LOGGER.warn("Configuration validation failed for accountId: {}", accountId, ex);
            throw ex;
        } catch (RuntimeException ex) {
            LOGGER.warn("Could not create translation service client with the current configuration for accountId: {}", accountId, ex);
            throw new InvalidConfigurationException(
                    ex.getMessage(),
                    // value in the errorDetail is null as we don't want to expose the account key
                    Stream.of(new ErrorDetail(AccountSettings.SAMPLE_ACCOUNT_SECRET, ErrorResponse.INVALID_KEY, null)).collect(Collectors.toList())
            );
        }
    }

    public List<ConfigurationValue> getConfigurationsForExternalConsumption(String accountId) throws ValidationException {
        AccountSettings accountSettings = accountSettingsRepository.findAccountSettings(accountId);
        if (accountSettings == null) {
            throw new ValidationException("Account " + accountId + " is not activated");
        }

        List<String> secretConfigurations = metadataService.getSecretConfigurations();
        if (accountSettings.getConfigurations() == null) {
            return Collections.emptyList();
        }

        List<ConfigurationValue> configurationValues = new ArrayList<>();
        for (Map.Entry<String, String> config : accountSettings.getConfigurations().entrySet()) {
            String externalValue = secretConfigurations.contains(config.getKey()) ? SECRET_VALUE : config.getValue();
            configurationValues.add(new ConfigurationValue(config.getKey(), externalValue));
        }

        return configurationValues;
    }

    public AccountSettings getSettings(String accountId) {
        return accountSettingsRepository.findAccountSettings(accountId);
    }

    private Map<String, String> getConfigurationValuesMap(ConfigurationValue[] configurations) {
        Map<String, String> values = new HashMap<>();
        for (ConfigurationValue gc : configurations) {
            Object value = gc.getValue();
            if (value != null) {
                values.put(gc.getId(), value.toString());
            }
        }
        return values;
    }

}
