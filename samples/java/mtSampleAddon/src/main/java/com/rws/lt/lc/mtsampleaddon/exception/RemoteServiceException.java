package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;

public class RemoteServiceException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public RemoteServiceException(ErrorResponse errorResponse) {
        super(errorResponse.toString());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
