package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

public class DoubleRegistrationException extends AppException {

    public DoubleRegistrationException() {
        super(ErrorResponse.ALREADY_REGISTERED_CODE, ErrorResponse.ALREADY_REGISTERED_MESSAGE);
    }
}
