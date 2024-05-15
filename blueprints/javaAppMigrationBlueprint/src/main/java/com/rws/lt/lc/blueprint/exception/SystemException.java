package com.rws.lt.lc.blueprint.exception;

import com.rws.lt.lc.blueprint.transfer.ErrorResponse;

public class SystemException extends AppException {

    public SystemException(String message) {
        super(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, message);
    }
}
