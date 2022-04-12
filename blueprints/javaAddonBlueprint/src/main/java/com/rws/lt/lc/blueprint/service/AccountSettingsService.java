package com.rws.lt.lc.blueprint.service;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import com.rws.lt.lc.blueprint.domain.ClientCredentials;
import com.rws.lt.lc.blueprint.exception.InvalidConfigurationException;
import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.exception.ValidationException;
import com.rws.lt.lc.blueprint.persistence.AccountSettingsRepository;
import com.rws.lt.lc.blueprint.transfer.ConfigurationValue;
import com.rws.lt.lc.blueprint.transfer.ErrorDetail;
import com.rws.lt.lc.blueprint.transfer.ErrorResponse;
import com.rws.lt.lc.blueprint.transfer.lifecycle.ActivatedEvent;
import com.rws.lt.lc.blueprint.transfer.lifecycle.ActivatedEventDetails;
import com.rws.lt.lc.blueprint.transfer.lifecycle.AddonLifecycleEvent;
import com.rws.lt.lc.blueprint.transfer.lifecycle.DeactivatedEvent;
import com.rws.lt.lc.blueprint.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AccountSettingsService {

    private static final String SECRET_VALUE = "***";

    private final AccountSettingsRepository accountSettingsRepository;
    private final AddonMetadataService metadataService;

    @Autowired
    public AccountSettingsService(AccountSettingsRepository accountSettingsRepository, AddonMetadataService metadataService) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.metadataService = metadataService;
    }

    public void handleAddonEvent(AddonLifecycleEvent lifecycleEvent) throws ValidationException {
        LOGGER.debug("addonLifecycleEvent >> with type {} at {}", lifecycleEvent.getId(), lifecycleEvent.getTimestamp());
        if (lifecycleEvent instanceof ActivatedEvent) {
            handleAddonEvent((ActivatedEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof DeactivatedEvent) {
            handleAddonEvent((DeactivatedEvent) lifecycleEvent);
        }
    }

    public void handleAddonEvent(ActivatedEvent lifecycleEvent) throws ValidationException {
        LOGGER.debug("handleAddonEvent >> for tenantId {} ", RequestLocalContext.getActiveAccountId());
        ActivatedEventDetails details = lifecycleEvent.getData();
        String tenantId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(tenantId);
        if (entity != null) {
            throw new ValidationException("Account already activated");
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
        accountSettingsRepository.save(entity);

        return getConfigurationsForExternalConsumption(accountId);
    }

    public void validateConfigurations(String accountId) throws NotAuthorizedException, InvalidConfigurationException {
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        if (entity == null) {
            throw new NotAuthorizedException("Account " + accountId + " is not activated");
        }

        var configurations = entity.getConfigurations();
        verifyNoInvalidValues(configurations);
        verifyNoInvalidKeys(configurations);
        verifyNoNullValues(configurations);
        verifySetup();
    }

    private void verifyNoInvalidValues(Map<String, String> configurations) throws InvalidConfigurationException {
        List<ErrorDetail> details = new ArrayList<>();
//        ErrorDetail errorDetail = new ErrorDetail("name", ErrorResponse.INVALID_VALUE, "value");
//        details.add(errorDetail);

        if (!details.isEmpty()) {
            throw new InvalidConfigurationException(ErrorResponse.INVALID_VALUE_MESSAGE, details);
        }
    }

    private void verifyNoInvalidKeys(Map<String, String> configurations) throws InvalidConfigurationException {
        List<ErrorDetail> details = new ArrayList<>();
//        ErrorDetail errorDetail = new ErrorDetail("name", ErrorResponse.INVALID_KEY, "key");
//        details.add(errorDetail);

        if (!details.isEmpty()) {
            throw new InvalidConfigurationException(ErrorResponse.INVALID_KEY_MESSAGE, details);
        }
    }

    private void verifyNoNullValues(Map<String, String> configurations) {
        List<ErrorDetail> details = new ArrayList<>();
//        ErrorDetail errorDetail = new ErrorDetail("name", ErrorResponse.NULL_VALUE, null);
//        details.add(errorDetail);

        if (!details.isEmpty()) {
            throw new InvalidConfigurationException(ErrorResponse.NULL_VALUE_MESSAGE, details);
        }
    }

    private void verifySetup() {
//        throw new InvalidConfigurationException(new ErrorResponse(ErrorResponse.INVALID_SETUP_MESSAGE, ErrorResponse.INVALID_SETUP_ERROR_CODE, new ArrayList<>()));
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

}
