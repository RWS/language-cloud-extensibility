package com.rws.lt.lc.mtsampleapp.web;

import com.rws.lt.lc.mtsampleapp.metadata.AppMetadataExtractionInterceptor;
import com.rws.lt.lc.mtsampleapp.security.AuthorizationRequestInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(value = "com.rws.lt.lc.mtsampleapp.web")
@AllArgsConstructor
public class WebControllerConfiguration implements WebMvcConfigurer {

    private final AuthorizationRequestInterceptor authorizationRequestInterceptor;
    private final AppMetadataExtractionInterceptor appMetadataExtractionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationRequestInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
        registry.addInterceptor(appMetadataExtractionInterceptor)
                .addPathPatterns("/**");
    }
}