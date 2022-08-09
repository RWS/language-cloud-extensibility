package com.rws.lt.lc.blueprint.service;

import com.rws.lt.lc.blueprint.domain.AccountSettings;
import com.rws.lt.lc.blueprint.domain.ClientCredentials;
import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.exception.ValidationException;
import com.rws.lt.lc.blueprint.persistence.AccountSettingsRepository;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AccountSettingsServiceTest {

    private static final String CREDS_ID = new ObjectId().toString();
    private static final String ACCOUNT_ID = "myAccount";

    @InjectMocks
    private AccountSettingsService accountSettingsService;

    @Mock
    private AccountSettingsRepository settingsRepository;

    @Mock
    private AddonMetadataService metadataService;

    @Before
    public void init(){
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
    public void testActivatedAddonEvent() {
        ActivatedEvent event = new ActivatedEvent();
        event.setTimestamp(Long.toString(System.currentTimeMillis()));
        ActivatedEventDetails details = new ActivatedEventDetails();

        details.setClientCredentials(new ClientCredentialsTO("clientId1", "clientSecret1"));
        event.setData(details);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.handleAddonEvent((AddonLifecycleEvent) event);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).save(settingsCaptor.capture());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));

        ClientCredentials clientCredentials = savedSettings.getClientCredentials();
        assertThat(clientCredentials.getClientId(), is("clientId1"));
        assertThat(clientCredentials.getClientSecret(), is("clientSecret1"));
    }

    @Test
    public void testActivatedAddonEventAlreadyActivated() {
        ActivatedEvent event = new ActivatedEvent();
        event.setTimestamp(Long.toString(System.currentTimeMillis()));
        ActivatedEventDetails details = new ActivatedEventDetails();

        event.setData(details);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(new AccountSettings());
        accountSettingsService.handleAddonEvent((AddonLifecycleEvent) event);

        verify(settingsRepository, never()).save(any());
    }

    @Test
    public void testDeactivatedAccountEvent() {
        DeactivatedEvent event = new DeactivatedEvent();
        event.setTimestamp(Long.toString(System.currentTimeMillis()));

        AccountSettings settings = new AccountSettings();
        settings.setAccountId(ACCOUNT_ID);

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(settings);
        accountSettingsService.handleAddonEvent(event);

        ArgumentCaptor<AccountSettings> settingsCaptor = ArgumentCaptor.forClass(AccountSettings.class);
        verify(settingsRepository, times(1)).delete(settingsCaptor.capture());
        AccountSettings savedSettings = settingsCaptor.getValue();

        assertThat(savedSettings.getAccountId(), is(ACCOUNT_ID));
    }

    @Test
    public void testDeactivatedAccountEventAlreadyDeactivated() {
        DeactivatedEvent event = new DeactivatedEvent();
        event.setTimestamp(Long.toString(System.currentTimeMillis()));

        when(settingsRepository.findAccountSettings(eq(ACCOUNT_ID))).thenReturn(null);
        accountSettingsService.handleAddonEvent(event);

        verify(settingsRepository).findAccountSettings(eq(ACCOUNT_ID));
        verifyNoMoreInteractions(settingsRepository);
    }

}
