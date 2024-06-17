package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.service.AccountSettingsService;
import com.rws.lt.lc.blueprint.transfer.lifecycle.AppLifecycleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Slf4j
public class LifecycleController {

    @Autowired
    private AccountSettingsService accountSettingsService;

    @PostMapping("/app-lifecycle")
    public void appLifecycleEvent(@RequestBody @Valid AppLifecycleEvent lifecycleEvent) {
        LOGGER.info("appLifecycleEvent >> with type {} at {}", lifecycleEvent.getId(), lifecycleEvent.getTimestamp());
        accountSettingsService.handleAppEvent(lifecycleEvent);
    }

}