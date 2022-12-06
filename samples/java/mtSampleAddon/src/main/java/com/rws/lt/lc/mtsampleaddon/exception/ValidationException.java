package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;

public class ValidationException extends AddonException {

    public ValidationException(String message) {
        super(ErrorResponse.VALIDATION_ERROR_CODE, message);
    }
}
