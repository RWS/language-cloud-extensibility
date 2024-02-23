package com.rws.lt.lc.blueprint.exception;

import com.rws.lt.lc.blueprint.transfer.ErrorResponse;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(ErrorResponse.NOT_FOUND_ERROR_CODE, message);
    }

}
