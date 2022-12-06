package com.rws.lt.lc.mtsampleaddon.persistence.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Map;

@Slf4j
public class LoggingRetryPolicy extends SimpleRetryPolicy {

    LoggingRetryPolicy(int retries, Map<Class<? extends Throwable>, Boolean> collect) {
        super(retries, collect);
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        super.registerThrowable(context, throwable);
        if (context.getRetryCount() > 1) {
            LOGGER.debug("Registered a new exception. New retry count is {}.", context.getRetryCount(), throwable);
        }
    }
}
