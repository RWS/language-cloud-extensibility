package com.rws.lt.lc.blueprint.metadata;

import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.rws.lt.lc.blueprint.metadata.AppMetadataConstants.*;

@Slf4j
@Component
public class AppMetadataExtractionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        addExtensionIdAndVersionToContext(request);
        return true;
    }

    private void addExtensionIdAndVersionToContext(HttpServletRequest request) {
        // access this property anywhere you need with RequestLocalContext.getFromLocalContext(EXTENSION_ID_HEADER)
        Optional.ofNullable(request.getHeader(EXTENSION_ID_HEADER))
                .ifPresent(extensionId -> RequestLocalContext.putInLocalContext(EXTENSION_ID_CONTEXT, extensionId));
        // access this property anywhere you need with RequestLocalContext.getFromLocalContext(EXTENSION_POINT_VERSION_CONTEXT)
        Optional.ofNullable(request.getHeader(EXTENSION_POINT_VERSION_HEADER))
                .ifPresent(extensionPointVersion -> RequestLocalContext.putInLocalContext(EXTENSION_POINT_VERSION_CONTEXT, extensionPointVersion));
        // access this property anywhere you need with RequestLocalContext.getFromLocalContext(ADDON_VERSION_CONTEXT)
        Optional.ofNullable(request.getHeader(APP_VERSION_HEADER))
                .ifPresent(addonVersion -> RequestLocalContext.putInLocalContext(APP_VERSION_CONTEXT, addonVersion));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // clean the local context
        RequestLocalContext.clean();
    }
}
