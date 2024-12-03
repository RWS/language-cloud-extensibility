package com.rws.lt.lc.blueprint.security;

import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.extensibility.security.JWSExtractor;
import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthorizationRequestInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JWSExtractor jwsExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;

        GenericAuthorization genericAuthorization = handlerMethod.getMethodAnnotation(GenericAuthorization.class);
        if (genericAuthorization != null) {
            // The point is that these endpoints will have a signature based on a generic LC public key (opposed to the account-specific ones)
            LOGGER.debug("Authorization passed for {}", request.getContextPath());
            return true;
        }

        return validateSignature(request);
    }

    private boolean validateSignature(HttpServletRequest request) throws NotAuthorizedException {

        try {
            jwsExtractor.extract(request);
        } catch (Exception e) {
            LOGGER.error("JWT extraction failed!", e);
            throw new NotAuthorizedException("Invalid authorization header");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // clean the local context
        RequestLocalContext.clean();
    }

}
