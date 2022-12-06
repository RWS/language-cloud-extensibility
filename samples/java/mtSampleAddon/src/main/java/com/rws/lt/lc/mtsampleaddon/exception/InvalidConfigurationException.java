package com.rws.lt.lc.mtsampleaddon.exception;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import com.rws.lt.lc.mtsampleaddon.transfer.ErrorDetail;

import java.util.List;

public class InvalidConfigurationException extends AddonException {

    public InvalidConfigurationException(String message) {
        super(ErrorResponse.INVALID_CONFIGURATION_ERROR_CODE, message);
    }

    public InvalidConfigurationException(String message, List<ErrorDetail> errorDetails) {
        super(ErrorResponse.INVALID_CONFIGURATION_ERROR_CODE, message, errorDetails);
    }

}
