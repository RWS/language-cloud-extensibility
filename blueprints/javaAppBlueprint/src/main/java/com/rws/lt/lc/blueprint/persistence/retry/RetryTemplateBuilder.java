package com.rws.lt.lc.blueprint.persistence.retry;

import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RetryTemplateBuilder {

    public static final long MINIMUM_ESTIMATED_INSERT_DURATION = 3L;

    public static RetryTemplate build(int retries, Set<Class<? extends Throwable>> retryExceptions) {
        RetryTemplate retryTemplate = new RetryTemplate();
        Map<Class<? extends Throwable>, Boolean> collect = retryExceptions.stream()
                .collect(Collectors.toMap(Function.identity(), (i) -> true, (i, j) -> j));
        retryTemplate.setRetryPolicy(new LoggingRetryPolicy(retries, collect));
        UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
        backOffPolicy.setMinBackOffPeriod(MINIMUM_ESTIMATED_INSERT_DURATION);
        backOffPolicy.setMaxBackOffPeriod(MINIMUM_ESTIMATED_INSERT_DURATION + 10);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
