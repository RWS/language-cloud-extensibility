package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

public class SystemException extends AppException {

    public SystemException(String message) {
        super(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, message);
    }
}
