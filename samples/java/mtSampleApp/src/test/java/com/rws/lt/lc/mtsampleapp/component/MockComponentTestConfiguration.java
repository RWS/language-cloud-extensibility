package com.rws.lt.lc.mtsampleapp.component;

import com.rws.lt.lc.extensibility.security.JWSExtractor;
import com.rws.lt.lc.extensibility.security.util.LocalContextKeys;
import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import com.rws.lt.lc.mtsampleapp.exception.NotAuthorizedException;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Profile("mock")
@Configuration
public class MockComponentTestConfiguration {

    private final String TEST_ACCOUNT_ID = "testAccount";

    @Bean
    @Primary
    public JWSExtractor jwsExtractor() throws NotAuthorizedException {
        JWSExtractor jwsExtractor = Mockito.mock(JWSExtractor.class);

        doAnswer(invocation -> {
            RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, TEST_ACCOUNT_ID);
            return null;
        }).when(jwsExtractor).extract(any(HttpServletRequest.class));

        return jwsExtractor;
    }


}
