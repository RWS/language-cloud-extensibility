package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

public class NotAuthorizedException extends AppException {

    public NotAuthorizedException(String message) {
        super(ErrorResponse.AUTHORIZATION_ERROR_CODE, message);
    }

}
