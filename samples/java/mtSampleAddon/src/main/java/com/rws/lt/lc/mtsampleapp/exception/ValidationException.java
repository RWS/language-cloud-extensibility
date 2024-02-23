package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(ErrorResponse.VALIDATION_ERROR_CODE, message);
    }
}
