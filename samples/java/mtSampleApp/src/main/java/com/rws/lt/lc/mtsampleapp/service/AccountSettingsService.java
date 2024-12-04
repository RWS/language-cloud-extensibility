package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import com.rws.lt.lc.mtsampleapp.domain.AccountSettings;
import com.rws.lt.lc.mtsampleapp.domain.AppRegistration;
import com.rws.lt.lc.mtsampleapp.domain.ClientCredentials;
import com.rws.lt.lc.mtsampleapp.exception.DoubleRegistrationException;
import com.rws.lt.lc.mtsampleapp.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleapp.exception.NotAuthorizedException;
import com.rws.lt.lc.mtsampleapp.exception.ValidationException;
import com.rws.lt.lc.mtsampleapp.persistence.AccountSettingsRepository;
import com.rws.lt.lc.mtsampleapp.persistence.AppRegistrationRepository;
import com.rws.lt.lc.mtsampleapp.transfer.ConfigurationValue;
import com.rws.lt.lc.mtsampleapp.transfer.ErrorDetail;
import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;
import com.rws.lt.lc.mtsampleapp.transfer.lifecycle.*;
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
    private final AppMetadataService metadataService;
    private final GoogleSupportedLanguagesService googleSupportedLanguagesService;
    private final AppRegistrationRepository appRegistrationRepository;

    @Autowired
    public AccountSettingsService(AccountSettingsRepository accountSettingsRepository, AppMetadataService metadataService, GoogleSupportedLanguagesService googleSupportedLanguagesService, AppRegistrationRepository appRegistrationRepository) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.metadataService = metadataService;
        this.googleSupportedLanguagesService = googleSupportedLanguagesService;
        this.appRegistrationRepository = appRegistrationRepository;
    }

    public void handleAppEvent(AppLifecycleEvent lifecycleEvent) {
        LOGGER.debug("appLifecycleEvent >> with type {} at {}", lifecycleEvent.getId(), lifecycleEvent.getTimestamp());
        if (lifecycleEvent instanceof InstalledEvent) {
            handleAppEvent((InstalledEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof RegisteredEvent) {
            handleAppEvent((RegisteredEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof UnregisteredEvent) {
            handleAppEvent((UnregisteredEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof UninstalledEvent) {
            handleAppEvent((UninstalledEvent) lifecycleEvent);
        }
    }

    public void handleAppEvent(InstalledEvent installedEvent) {
        LOGGER.debug("handleAppEvent >> for tenantId {} ", RequestLocalContext.getActiveAccountId());
        var tenantId = RequestLocalContext.getActiveAccountId();
        var entity = accountSettingsRepository.findAccountSettings(tenantId);

        if (entity != null) {
            LOGGER.debug("App already activated for tenantId {}", tenantId);
            return;
        }

        entity = new AccountSettings();
        entity.setAccountId(tenantId);
        entity.setRegion(installedEvent.getData().getRegion());

        accountSettingsRepository.save(entity);
    }

    public void handleAppEvent(UninstalledEvent uninstalledEvent) {
        String accountId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        if (entity != null) {
            accountSettingsRepository.delete(entity);
        }
    }

    public void handleAppEvent(RegisteredEvent registeredEvent) {
        var details = registeredEvent.getData();
        var tenantId = RequestLocalContext.getActiveAccountId();
        var entity = appRegistrationRepository.findByAccountId(tenantId);

        if (entity != null) {
            LOGGER.debug("App already registered for tenantId {}", tenantId);
            throw new DoubleRegistrationException();
        }

        entity = new AppRegistration();
        entity.setAccountId(tenantId);
        if (details.getClientCredentials() != null) {
            entity.setClientCredentials(new ClientCredentials(details.getClientCredentials()));
        }

        appRegistrationRepository.save(entity);
    }

    public void handleAppEvent(UnregisteredEvent lifecycleEvent) {
        var tenantId = RequestLocalContext.getActiveAccountId();
        appRegistrationRepository.deleteByAccountId(tenantId);
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
