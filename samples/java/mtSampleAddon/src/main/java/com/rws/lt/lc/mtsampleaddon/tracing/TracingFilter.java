package com.rws.lt.lc.mtsampleaddon.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class TracingFilter implements Filter {

    @Autowired
    private TracingHeadersUtils tracingUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        tracingUtils.getOrGenerateTracingIdFromHeaders((HttpServletRequest) request);
        tracingUtils.addTracingIdToHeader((HttpServletResponse) response);
        LOGGER.debug("Initialized trace id");

        try {
            filterChain.doFilter(request, response);
        } finally {
            TracingLocalContext.clean();
        }
    }

    @Override
    public void destroy() {
    }
}

