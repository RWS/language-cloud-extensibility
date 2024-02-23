package com.rws.lt.lc.blueprint.exception;

import com.rws.lt.lc.blueprint.transfer.ErrorResponse;
import com.rws.lt.lc.blueprint.transfer.ErrorDetail;

import java.util.List;

public class InvalidConfigurationException extends AppException {

    public InvalidConfigurationException(String message, List<ErrorDetail> errorDetails) {
        super(ErrorResponse.INVALID_CONFIGURATION_ERROR_CODE, message, errorDetails);
    }

}
