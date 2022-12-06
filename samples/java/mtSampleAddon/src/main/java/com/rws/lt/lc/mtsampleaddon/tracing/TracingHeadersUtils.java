package com.rws.lt.lc.mtsampleaddon.tracing;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class TracingHeadersUtils {
    public static final String TRACING_ID_HEADER = "TR_ID";

    /**
     * Reads tracing id from header and adds it to local context.
     * If no header is found, a new tracing id is generated and added to the local context.
     *
     * @param httpRequest incoming http request.
     */
    public void getOrGenerateTracingIdFromHeaders(HttpServletRequest httpRequest) {
        String tracingId = httpRequest.getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        if (tracingId == null) {
            tracingId = TracingLocalContext.getTracingId();
            if (tracingId == null) {
                tracingId = UUID.randomUUID().toString();
                TracingLocalContext.setTracingId(tracingId);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Generate a new {} with value: {} for {} {}", TracingHeadersUtils.TRACING_ID_HEADER, tracingId, httpRequest.getMethod(),
                            httpRequest.getRequestURL().append('?').append(httpRequest.getQueryString()).toString());
                }
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{} : {} was received for {}.", TracingHeadersUtils.TRACING_ID_HEADER, tracingId, httpRequest.getRequestURL().append('?')
                            .append(httpRequest.getQueryString()).toString());
                }
            }
        } else {
            TracingLocalContext.setTracingId(tracingId);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("{} : {} was received for {}.", TracingHeadersUtils.TRACING_ID_HEADER, tracingId, httpRequest.getRequestURL().append('?')
                        .append(httpRequest.getQueryString()).toString());
            }
        }
    }

    /**
     * Reads tracing id header from the local context and adds it to the http response headers.
     * If no tracing id is found, the http headers are not updated.
     *
     * @param response outgoing http response
     */
    public void addTracingIdToHeader(HttpServletResponse response) {
        if (response != null) {
            String tracingId = TracingLocalContext.getTracingId();
            if (tracingId != null) {
                response.addHeader(TRACING_ID_HEADER, tracingId);
            }
        }
    }

}
