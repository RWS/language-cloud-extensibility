package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.exception.ValidationException;
import com.rws.lt.lc.blueprint.service.AccountSettingsService;
import com.rws.lt.lc.blueprint.transfer.ConfigurationSettingsResult;
import com.rws.lt.lc.blueprint.transfer.ConfigurationValue;
import com.rws.lt.lc.blueprint.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/configuration")
@Slf4j
public class ConfigurationController {

    @Autowired
    private AccountSettingsService accountSettingsService;

    @PostMapping
    public ConfigurationSettingsResult saveConfigurationSettings(@RequestBody @Valid ConfigurationValue[] configurations) throws ValidationException, NotAuthorizedException {
        LOGGER.info("saveConfigurationSettings >>");
        List<ConfigurationValue> configurationValues = accountSettingsService.saveOrUpdateConfigurations(RequestLocalContext.getActiveAccountId(), configurations);
        return new ConfigurationSettingsResult(configurationValues);
    }

    @GetMapping
    public ConfigurationSettingsResult getConfigurationSettings() throws ValidationException {
        LOGGER.info("getConfigurationSettings >>");

        List<ConfigurationValue> configurationValues = accountSettingsService.getConfigurationsForExternalConsumption(RequestLocalContext.getActiveAccountId());
        return new ConfigurationSettingsResult(configurationValues);
    }

    @PostMapping("/validation")
    @ResponseStatus(HttpStatus.OK)
    public void validateConfigurationSettings() throws ValidationException, NotAuthorizedException {
        LOGGER.info("validateConfigurationSettings >>");
        accountSettingsService.validateConfigurations(RequestLocalContext.getActiveAccountId());
    }

}
