package com.rws.lt.lc.blueprint.exception;

import com.rws.lt.lc.blueprint.transfer.ErrorResponse;

public class ValidationException extends AddonException {

    public ValidationException(String message) {
        super(ErrorResponse.VALIDATION_ERROR_CODE, message);
    }
}
