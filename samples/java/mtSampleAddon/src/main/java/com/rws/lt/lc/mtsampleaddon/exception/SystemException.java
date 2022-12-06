package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;

public class SystemException extends AddonException {

    public SystemException(String message) {
        super(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, message);
    }
}
