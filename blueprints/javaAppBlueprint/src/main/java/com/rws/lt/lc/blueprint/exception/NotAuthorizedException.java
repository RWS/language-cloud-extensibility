package com.rws.lt.lc.blueprint.exception;

import com.rws.lt.lc.blueprint.transfer.ErrorResponse;

public class NotAuthorizedException extends AppException {

    public NotAuthorizedException(String message) {
        super(ErrorResponse.AUTHORIZATION_ERROR_CODE, message);
    }

}
