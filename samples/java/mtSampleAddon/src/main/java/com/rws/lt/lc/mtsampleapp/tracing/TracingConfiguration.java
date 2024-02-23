package com.rws.lt.lc.mtsampleapp.tracing;

import com.rws.lt.lc.mtsampleapp.web.ServletFilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TracingConfiguration {

    @Bean
    public TracingHeadersUtils tracingHeadersUtils() {
        return new TracingHeadersUtils();
    }

    @Bean
    public TracingFilter tracingFilter() {
        return new TracingFilter();
    }

    @Bean
    public FilterRegistrationBean tracingFilterRegistrationBean() {

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setEnabled(true);
        registrationBean.setOrder(ServletFilterOrder.TRACING_FILTER_ORDER);

        registrationBean.setFilter(tracingFilter());

        LOGGER.info("Registered Tracing filter with Spring");

        return registrationBean;
    }

}

