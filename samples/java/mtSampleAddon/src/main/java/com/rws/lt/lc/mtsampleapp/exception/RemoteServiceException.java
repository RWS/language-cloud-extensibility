package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;

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
