package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorResponse;
import com.rws.lt.lc.mtsampleapp.transfer.ErrorDetail;

import java.util.List;

public class InvalidConfigurationException extends AppException {

    public InvalidConfigurationException(String message) {
        super(ErrorResponse.INVALID_CONFIGURATION_ERROR_CODE, message);
    }

    public InvalidConfigurationException(String message, List<ErrorDetail> errorDetails) {
        super(ErrorResponse.INVALID_CONFIGURATION_ERROR_CODE, message, errorDetails);
    }

}
