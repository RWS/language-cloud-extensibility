package com.rws.lt.lc.blueprint.service;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import com.rws.lt.lc.blueprint.domain.AppRegistration;
import com.rws.lt.lc.blueprint.domain.ClientCredentials;
import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.exception.NotFoundException;
import com.rws.lt.lc.blueprint.exception.ValidationException;
import com.rws.lt.lc.blueprint.persistence.AccountSettingsRepository;
import com.rws.lt.lc.blueprint.persistence.AppRegistrationRepository;
import com.rws.lt.lc.blueprint.transfer.ConfigurationValue;
import com.rws.lt.lc.blueprint.transfer.lifecycle.*;
import com.rws.lt.lc.blueprint.util.LocalContextKeys;
import com.rws.lt.lc.blueprint.util.RequestLocalContext;
import org.bson.types.ObjectId;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.APP_ID_CONTEXT;
import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.DEV_TENANT_ID_CONTEXT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AccountSettingsServiceTest {

    private static final String CREDS_ID = new ObjectId().toString();
    private static final String ACCOUNT_ID = "myAccount";
    private static final String APP_ID = "lcAppId";

    @InjectMocks
    private AccountSettingsService accountSettingsService;

    @Mock
    private AccountSettingsRepository settingsRepository;

    @Mock
    private AppRegistrationRepository appRegistrationRepository;

    @Mock
    private AppMetadataService metadataService;

    @Before
    public void init() {
        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
    }

    @Test
    public void testSetNewConfigurations() throws ValidationException, NotAuthorizedException {
        AccountSettings persisted = new AccountSettings();
        persisted.setId(CREDS_ID);
        persisted.setAccountId(ACCOUNT_ID);
        Map<String, String> configs = new HashMap<>();
        configs.put("config1", "value1");
        persisted.setConfigurations(configs);

        AccountSettings emptySettings = getEmptyAccountSettings();

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(emptySettings, persisted);
        when(metadataService.getSecretConfigurations()).thenReturn(Collections.emptyList());
        accountSettingsService.saveOrUpdateConfigurations(ACCOUNT_ID, new ConfigurationValue[]{new ConfigurationValue("config1", "value1")});

        ArgumentCaptor<AccountSettings> captor = ArgumentCaptor.forClass(AccountSettings.class);
        Mockito.verify(settingsRepository, times(1)).save(captor.capture());

        AccountSettings entity = captor.getValue();
        assertThat(entity, is(notNullValue()));
        assertThat(entity.getAccountId(), is(ACCOUNT_ID));
        assertThat(entity.getConfigurations(), aMapWithSize(1));
        assertThat(entity.getConfigurations().get("config1"), is("value1"));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testSetNewConfigurationsNullAccountSettings() throws ValidationException, NotAuthorizedException {
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.saveOrUpdateConfigurations(ACCOUNT_ID, new ConfigurationValue[]{new ConfigurationValue("config1", "value1")});
    }

    private AccountSettings getEmptyAccountSettings() {
        AccountSettings emptySettings = new AccountSettings();
        emptySettings.setAccountId(ACCOUNT_ID);
        return emptySettings;
    }

    @Test
    public void testGetSettings() {
        AccountSettings persisted = new AccountSettings();
        persisted.setId(CREDS_ID);
        persisted.setAccountId(ACCOUNT_ID);

        Map<String, String> configs = new HashMap<>();
        configs.put("config1", "value1");
        persisted.setConfigurations(configs);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(persisted);

        AccountSettings credentials = accountSettingsService.getSettings(ACCOUNT_ID);
        assertThat(persisted, is(credentials));
    }

    @Test
    public void testGetCredentialsForExternalConsumption() throws ValidationException {
        AccountSettings persisted = new AccountSettings();
        persisted.setId(CREDS_ID);
        persisted.setAccountId(ACCOUNT_ID);

        Map<String, String> configs = new HashMap<>();
        configs.put("config1", "value1");
        configs.put("config2", "value2");
        persisted.setConfigurations(configs);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(persisted);
        when(metadataService.getSecretConfigurations()).thenReturn(Collections.singletonList("config2"));

        List<ConfigurationValue> retrievedConfigs = accountSettingsService.getConfigurationsForExternalConsumption(ACCOUNT_ID);
        assertThat(retrievedConfigs, hasSize(2));

        ConfigurationValue[] expectedConfigs = {new ConfigurationValue("config1", "value1"),
                new ConfigurationValue("config2", "***")};

        assertThat(retrievedConfigs, containsInAnyOrder(expectedConfigs));
    }

    @Test(expected = ValidationException.class)
    public void testGetCredentialsForExternalConsumptionAccountNotActivated() throws ValidationException {
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);

        accountSettingsService.getConfigurationsForExternalConsumption(ACCOUNT_ID);
    }


    @Test
    public void testGetCredentialsForExternalConsumptionNoCredentials() throws ValidationException {
        AccountSettings persisted = new AccountSettings();
        persisted.setId(CREDS_ID);
        persisted.setAccountId(ACCOUNT_ID);
        persisted.setConfigurations(null);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(persisted);

        List<ConfigurationValue> retrievedConfigs = accountSettingsService.getConfigurationsForExternalConsumption(ACCOUNT_ID);
        assertThat(retrievedConfigs, hasSize(0));

    }

    @Test
    public void testGetInexistentCredentials() {
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);

        AccountSettings credentials = accountSettingsService.getSettings(ACCOUNT_ID);
        assertThat(credentials, IsNull.nullValue());
    }

    @Test
    public void testInstalledAppEvent() {
        var installedEvent = new InstalledEvent();
        installedEvent.setTimestamp(Long.toString(System.currentTimeMillis()));
        var appRegistration = new AppRegistration();

        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.handleAppEvent(installedEvent);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).save(settingsCaptor.capture());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));
    }

    @Test
    public void testInstalledAppEventWithRegistrationUpdate() {
        var installedEvent = new InstalledEvent();
        installedEvent.setTimestamp(Long.toString(System.currentTimeMillis()));
        var appRegistration = new AppRegistration();

        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        when(appRegistrationRepository.findFirst()).thenReturn(Optional.of(appRegistration));
        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));
        accountSettingsService.handleAppEvent(installedEvent);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).save(settingsCaptor.capture());
        verify(appRegistrationRepository).save(any());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));
    }

    @Test
    public void testInstallAppEventAlreadyInstalled() {
        var installedEvent = new InstalledEvent();
        installedEvent.setTimestamp(Long.toString(System.currentTimeMillis()));
        var appRegistration = new AppRegistration();

        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.handleAppEvent(installedEvent);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).save(settingsCaptor.capture());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));
    }

    @Test
    public void testUninstalledAccountEvent() {
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        var appRegistration = new AppRegistration();
        appRegistration.setAccountId(ACCOUNT_ID);
        appRegistration.setAccountId(APP_ID);

        var uninstalledEvent = new UninstalledEvent();
        uninstalledEvent.setTimestamp(Long.toString(System.currentTimeMillis()));

        AccountSettings settings = new AccountSettings();
        settings.setAccountId(ACCOUNT_ID);

        when(appRegistrationRepository.findFirst()).thenReturn(Optional.of(appRegistration));
        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(settings);
        accountSettingsService.handleAppEvent(uninstalledEvent);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).delete(settingsCaptor.capture());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));
    }

    @Test
    public void testUninstalledAccountEventAlreadyUninstalled() {
        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        var appRegistration = new AppRegistration();
        appRegistration.setAccountId(ACCOUNT_ID);
        appRegistration.setAccountId(APP_ID);

        UninstalledEvent event = new UninstalledEvent();
        event.setTimestamp(Long.toString(System.currentTimeMillis()));

        when(appRegistrationRepository.findFirst()).thenReturn(Optional.of(appRegistration));
        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));
        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.handleAppEvent(event);

        verify(settingsRepository).findAccountSettings(eq(ACCOUNT_ID));
        verifyNoMoreInteractions(settingsRepository);
    }

    @Test
    public void testRegisteredAppEvent() {
        var registeredEvent = new RegisteredEvent();
        registeredEvent.setTimestamp(Long.toString(System.currentTimeMillis()));
        var details = new RegisteredEventDetails();
        details.setClientCredentials(new ClientCredentialsTO("clientId1", "clientSecret1"));
        registeredEvent.setData(details);

        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);
        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.empty());
        accountSettingsService.handleAppEvent(registeredEvent);

        ArgumentCaptor<AppRegistration> appRegistrationArgumentCaptor = ArgumentCaptor.forClass(AppRegistration.class);
        verify(appRegistrationRepository, times(1)).save(appRegistrationArgumentCaptor.capture());
        AppRegistration savedAppRegistration = appRegistrationArgumentCaptor.getValue();

        assertThat(savedAppRegistration.getAccountId(), is(ACCOUNT_ID));

        ClientCredentials clientCredentials = savedAppRegistration.getClientCredentials();
        assertThat(clientCredentials.getClientId(), is("clientId1"));
        assertThat(clientCredentials.getClientSecret(), is("clientSecret1"));
    }

    @Test
    public void testRegisteredAppEventAlreadyRegistered() {
        var registeredEvent = new RegisteredEvent();
        var registeredApp = new AppRegistration();
        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);
        when(appRegistrationRepository.findRegistration(ACCOUNT_ID, APP_ID)).thenReturn(Optional.of(registeredApp));

        accountSettingsService.handleAppEvent(registeredEvent);

        verify(appRegistrationRepository, never()).save(any());
    }

    @Test
    public void testUnregisteredAppEvent() {
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);

        var appRegistration = new AppRegistration();
        appRegistration.setAccountId(ACCOUNT_ID);
        appRegistration.setAccountId(APP_ID);

        when(appRegistrationRepository.findFirst()).thenReturn(Optional.of(appRegistration));
        when(appRegistrationRepository.findRegistration(eq(ACCOUNT_ID), eq(APP_ID))).thenReturn(Optional.of(appRegistration));

        var unregisteredEvent = new UnregisteredEvent();
        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);

        accountSettingsService.handleAppEvent(unregisteredEvent);

        verify(appRegistrationRepository).deleteRegistration(eq(ACCOUNT_ID), eq(APP_ID));
    }

    @Test(expected = NotFoundException.class)
    public void testUnregisteredDifferentApp() {
        var unregisteredEvent = new UnregisteredEvent();
        RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(DEV_TENANT_ID_CONTEXT, ACCOUNT_ID);
        RequestLocalContext.putInLocalContext(APP_ID_CONTEXT, APP_ID);
        when(appRegistrationRepository.findRegistration(ACCOUNT_ID, APP_ID)).thenReturn(Optional.empty());

        accountSettingsService.handleAppEvent(unregisteredEvent);

        verify(appRegistrationRepository, never()).deleteRegistration(any(), any());
    }
}