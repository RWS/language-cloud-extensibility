package com.rws.lt.lc.mtsampleaddon.transfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class ErrorResponse {

    public static final String INVALID_CODE = "invalid";
    public static final String EMPTY_CODE = "empty";
    public static final String MISSING_CODE = "missing";
    public static final String MAX_SIZE_CODE = "maxSize";
    public static final String MIN_SIZE_CODE = "minSize";
    public static final String INVALID_VALUE = "invalidValue";
    public static final String INVALID_KEY = "invalidKey";
    public static final String NULL_VALUE = "nullValue";

    public static final String GENERIC_APPLICATION_ERROR_CODE = "genericApplicationException";
    public static final String NOT_FOUND_ERROR_CODE = "entityNotFoundException";
    public static final String VALIDATION_ERROR_CODE = "validationException";
    public static final String INPUT_PARSING_ERROR_CODE = "inputParsingException";
    public static final String AUTHORIZATION_ERROR_CODE = "authorizationException";
    public static final String INVALID_CONFIGURATION_ERROR_CODE = "invalidConfiguration";
    public static final String INVALID_SETUP_ERROR_CODE = "invalidSetup";

    public static final String GENERIC_APPLICATION_MESSAGE = "An error occurred while processing your request!";
    public static final String VALIDATION_MESSAGE = "Input validation errors";
    public static final String MISSING_PARAMETER_MESSAGE = "Missing parameter";
    public static final String OPTIMISTIC_LOCKING_FAILURE_EXCEPTION_MESSAGE = "Unable to reconcile after optimistic lock failure.";
    public static final String INVALID_VALUE_MESSAGE = "Invalid value.";
    public static final String INVALID_KEY_MESSAGE = "Invalid key.";
    public static final String NULL_VALUE_MESSAGE = "Value cannot be null.";
    public static final String INVALID_SETUP_MESSAGE = "Invalid setup.";
    public static final String NOT_CONFIGURED_MESSAGE = "Missing configuration settings";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server exception";

    @JsonIgnore
    private int statusCode;

    private String message;
    private String errorCode;
    private List<ErrorDetail> details;

    public ErrorResponse(String errorCode, String message, List<ErrorDetail> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    public ErrorResponse(String message, String errorCode, List<ErrorDetail> details, int statusCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.statusCode = statusCode;
    }
}
