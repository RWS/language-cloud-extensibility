package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;

public class NotAuthorizedException extends AddonException {

    public NotAuthorizedException(String message) {
        super(ErrorResponse.AUTHORIZATION_ERROR_CODE, message);
    }

}
