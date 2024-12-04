package com.rws.lt.lc.mtsampleapp.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.rws.lt.lc.extensibility.security.util.LocalContextKeys;
import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return (String) Optional.ofNullable(
                RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID)
        ).orElse("guest");
    }

}