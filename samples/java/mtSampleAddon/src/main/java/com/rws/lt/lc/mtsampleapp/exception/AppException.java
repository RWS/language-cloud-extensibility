package com.rws.lt.lc.mtsampleapp.exception;

import com.rws.lt.lc.mtsampleapp.transfer.ErrorDetail;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class AppException extends RuntimeException {

    private final String errorCode;
    protected List<ErrorDetail> details = new ArrayList<>();

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String message, List<ErrorDetail> errorDetails) {
        super(message);
        this.errorCode = errorCode;
        this.details = errorDetails;
    }

    public void addError(ErrorDetail propertyValidationError) {
        this.details.add(propertyValidationError);
    }
}

