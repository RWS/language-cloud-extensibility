package com.rws.lt.lc.blueprint.tracing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TracingLocalContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<String>();

    public static String getTracingId() {
        return CONTEXT.get();
    }

    public static void setTracingId(String tracingId) {
        CONTEXT.set(tracingId);
    }

    public static void clean() {
        LOGGER.debug("Cleaning tracing local context");
        CONTEXT.remove();
    }
}
