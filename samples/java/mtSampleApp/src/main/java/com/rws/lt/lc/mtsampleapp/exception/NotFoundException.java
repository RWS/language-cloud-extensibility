package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(ErrorResponse.NOT_FOUND_ERROR_CODE, message);
    }

}
