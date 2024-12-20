package com.rws.lt.lc.blueprint.service;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import com.rws.lt.lc.blueprint.domain.AppRegistration;
import com.rws.lt.lc.blueprint.domain.ClientCredentials;
import com.rws.lt.lc.blueprint.exception.InvalidConfigurationException;
import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.exception.NotFoundException;
import com.rws.lt.lc.blueprint.exception.ValidationException;
import com.rws.lt.lc.blueprint.persistence.AccountSettingsRepository;
import com.rws.lt.lc.blueprint.persistence.AppRegistrationRepository;
import com.rws.lt.lc.blueprint.transfer.ConfigurationValue;
import com.rws.lt.lc.blueprint.transfer.ErrorDetail;
import com.rws.lt.lc.blueprint.transfer.ErrorResponse;
import com.rws.lt.lc.blueprint.transfer.lifecycle.*;
import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.APP_ID_CONTEXT;
import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.DEV_TENANT_ID_CONTEXT;

@Slf4j
@Service
public class AccountSettingsService {

    private static final String SECRET_VALUE = "***";

    private final AccountSettingsRepository accountSettingsRepository;
    private final AppRegistrationRepository appRegistrationRepository;
    private final AppMetadataService metadataService;

    @Autowired
    public AccountSettingsService(AccountSettingsRepository accountSettingsRepository, AppRegistrationRepository appRegistrationRepository, AppMetadataService metadataService) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.appRegistrationRepository = appRegistrationRepository;
        this.metadataService = metadataService;
    }

    public void handleAppEvent(AppLifecycleEvent lifecycleEvent) {
        LOGGER.debug("appLifecycleEvent >> with type {} at {}", lifecycleEvent.getId(), lifecycleEvent.getTimestamp());
        if (lifecycleEvent instanceof InstalledEvent) {
            validateLifeCycleEvent();
            handleAppEvent((InstalledEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof RegisteredEvent) {
            // can't validate lifecycle until the app is registered
            handleAppEvent((RegisteredEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof UninstalledEvent) {
            validateLifeCycleEvent();
            handleAppEvent((UninstalledEvent) lifecycleEvent);
        } else if (lifecycleEvent instanceof UnregisteredEvent) {
            validateLifeCycleEvent();
            handleAppEvent((UnregisteredEvent) lifecycleEvent);
        }
    }

    private void handleAppEvent(InstalledEvent lifecycleEvent) {
        LOGGER.debug("handleAppEvent >> for tenantId {} ", RequestLocalContext.getActiveAccountId());
        var tenantId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(tenantId);

        if (entity != null) {
            LOGGER.debug("App already activated for tenantId {}", tenantId);
            return;
        }

        entity = new AccountSettings();
        entity.setAccountId(tenantId);
        entity.setRegion(lifecycleEvent.getData().getRegion());

        accountSettingsRepository.save(entity);
    }

    private void handleAppEvent(UninstalledEvent lifecycleEvent) {
        String accountId = RequestLocalContext.getActiveAccountId();
        AccountSettings entity = accountSettingsRepository.findAccountSettings(accountId);
        if (entity != null) {
            accountSettingsRepository.delete(entity);
        }
    }

    private void handleAppEvent(RegisteredEvent registeredEvent) {
        var details = registeredEvent.getData();
        var tenantId = RequestLocalContext.getActiveAccountId();
        var appId = (String) RequestLocalContext.getFromLocalContext(APP_ID_CONTEXT);
        var existingRegistration = appRegistrationRepository.findRegistration(tenantId, appId);
        if (existingRegistration.isPresent()) {
            LOGGER.debug("App already registered for tenantId {}", tenantId);
            return;
        }

        var appRegistration = new AppRegistration();
        appRegistration.setAccountId(tenantId);
        appRegistration.setAppId(appId);
        if (details.getClientCredentials() != null) {
            appRegistration.setClientCredentials(new ClientCredentials(details.getClientCredentials()));
        }

        appRegistrationRepository.save(appRegistration);
    }

    private void handleAppEvent(UnregisteredEvent lifecycleEvent) {
        var tenantId = RequestLocalContext.getActiveAccountId();
        var appId = (String) RequestLocalContext.getFromLocalContext(APP_ID_CONTEXT);

        appRegistrationRepository.deleteRegistration(tenantId, appId);
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

    private void validateLifeCycleEvent() {
        String tenantId = (String) RequestLocalContext.getFromLocalContext(DEV_TENANT_ID_CONTEXT);
        String appId = (String) RequestLocalContext.getFromLocalContext(APP_ID_CONTEXT);
        var existingAppRegistration = appRegistrationRepository.findFirst();
        existingAppRegistration.ifPresent(registration -> {
            // for apps already registered without those 2 fields set
            if (StringUtils.isEmpty(registration.getAppId()) || StringUtils.isEmpty(registration.getAccountId())) {
                registration.setAppId(appId);
                registration.setAccountId(tenantId);
                appRegistrationRepository.save(registration);
            }
        });

        var appRegistration = appRegistrationRepository.findRegistration(tenantId, appId);
        if (appRegistration.isEmpty()) {
            LOGGER.warn("Could not find registration for tenantId {} and appId {}", tenantId, appId);
            throw new NotFoundException(String.format("Could not find registration for tenantId %s and appId %s", tenantId, appId));
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

}