package com.rws.lt.lc.mtsampleaddon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rws.lt.lc.mtsampleaddon.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
public class RequestLocalContext {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ThreadLocal<Map> CONTEXT = new ThreadLocal<Map>() {
        protected Map initialValue() {
            Map localMap = new HashMap();
            localMap.put(LocalContextKeys.CONVERSATION_CONTEXT, new HashMap());
            localMap.put(LocalContextKeys.RENDER_FLAGS, new HashSet<RenderableFlag>());
            return localMap;
        };
    };

    public static void putInLocalContext(Object key, Object value) {
        Map localMap = CONTEXT.get();
        localMap.put(key, value);
    }

    public static Object getFromLocalContext(Object key) {
        Map localMap = CONTEXT.get();
        return localMap.get(key);
    }

    public static void clean() {
        LOGGER.debug("Cleaning thread local context");
        CONTEXT.remove();
    }

    public static String getActiveAccountId() throws SystemException {
        String accountId = (String) CONTEXT.get().get(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        if (accountId == null) {
            throw new SystemException("No active account id found.");
        }
        return accountId;
    }

    public static Map getLocalContextMap() {
        return (Map)CONTEXT.get();
    }
}
