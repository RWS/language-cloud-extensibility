package com.rws.lt.lc.mtsampleapp.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.rws.lt.lc.mtsampleapp.tracing.TracingLocalContext;

public class TraceIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String traceId = TracingLocalContext.getTracingId();
        if (traceId != null) {
            return traceId;
        }
        return "undefined";
    }

}