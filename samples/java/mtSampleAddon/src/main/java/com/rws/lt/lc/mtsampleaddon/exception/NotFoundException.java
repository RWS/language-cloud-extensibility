package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;

public class NotFoundException extends AddonException {

    public NotFoundException(String message) {
        super(ErrorResponse.NOT_FOUND_ERROR_CODE, message);
    }

}
