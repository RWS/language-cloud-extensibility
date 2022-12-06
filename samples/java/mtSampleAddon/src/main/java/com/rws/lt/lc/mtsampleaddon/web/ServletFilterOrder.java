package com.rws.lt.lc.mtsampleaddon.web;

import org.springframework.core.Ordered;

public class ServletFilterOrder {

    public static final int TRACING_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;
    public static final int ACCOUNT_ID_FILTER_ORDER = TRACING_FILTER_ORDER + 1;

    private ServletFilterOrder() {
    }


}
