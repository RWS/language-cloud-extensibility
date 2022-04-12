package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.security.AuthorizationRequestInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(value = "com.rws.lt.lc.blueprint.web")
public class WebControllerConfiguration extends WebMvcConfigurerAdapter {

    private final AuthorizationRequestInterceptor authorizationRequestInterceptor;

    public WebControllerConfiguration(AuthorizationRequestInterceptor authorizationRequestInterceptor) {
        this.authorizationRequestInterceptor = authorizationRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationRequestInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}