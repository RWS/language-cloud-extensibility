package com.rws.lt.lc.blueprint.web;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.rws.lt.lc.blueprint.exception.*;
import com.rws.lt.lc.blueprint.transfer.ErrorDetail;
import com.rws.lt.lc.blueprint.transfer.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Map<String, String> VALIDATION_CODE_MAPPINGS = new LinkedHashMap<>();

    static {
        VALIDATION_CODE_MAPPINGS.put(NotNull.class.getSimpleName(), ErrorResponse.EMPTY_CODE);
        VALIDATION_CODE_MAPPINGS.put(Max.class.getSimpleName(), ErrorResponse.MAX_SIZE_CODE);
        VALIDATION_CODE_MAPPINGS.put(Min.class.getSimpleName(), ErrorResponse.MIN_SIZE_CODE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        LOGGER.error("Caught exception, returning generic application error.", ex);
        return new ErrorResponse(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, ErrorResponse.GENERIC_APPLICATION_MESSAGE, Collections.emptyList());
    }

    @ExceptionHandler(SystemException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemException(SystemException ex) {
        return new ErrorResponse(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return new ErrorResponse(ErrorResponse.NOT_FOUND_ERROR_CODE, ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedException(NotAuthorizedException ex) {
        return new ErrorResponse(ErrorResponse.AUTHORIZATION_ERROR_CODE, ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(RemoteServiceException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handle(RemoteServiceException ex) {
        return new ResponseEntity<>(
                ex.getErrorResponse(),
                HttpStatus.valueOf(ex.getErrorResponse().getStatusCode())
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle() {
        return new ErrorResponse(ErrorResponse.NOT_FOUND_ERROR_CODE, ErrorResponse.OPTIMISTIC_LOCKING_FAILURE_EXCEPTION_MESSAGE, Collections.emptyList());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleValidationException(ValidationException ex) {
        return new ErrorResponse(ErrorResponse.VALIDATION_ERROR_CODE, ex.getMessage(), ex.getDetails());
    }

    @ExceptionHandler(InvalidConfigurationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConfigValidationException(InvalidConfigurationException ex) {
        return new ErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getDetails());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(ErrorResponse.VALIDATION_ERROR_CODE, ErrorResponse.MISSING_PARAMETER_MESSAGE, missingServletRequestParameterErrorToErrorDetails(ex.getParameterName())),
                headers,
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(ErrorResponse.VALIDATION_ERROR_CODE, ErrorResponse.VALIDATION_MESSAGE, bindingResultErrorsToErrorDetails(ex.getBindingResult())),
                headers,
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(ErrorResponse.VALIDATION_ERROR_CODE, ErrorResponse.VALIDATION_MESSAGE, bindingResultErrorsToErrorDetails(ex)),
                headers,
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) ex.getCause();
            String field = jsonMappingException.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
            Object value = "";
            if (jsonMappingException instanceof InvalidFormatException) {
                value = ((InvalidFormatException) jsonMappingException).getValue();
            }

            return new ResponseEntity<>(
                    new ErrorResponse(ErrorResponse.VALIDATION_ERROR_CODE, ErrorResponse.VALIDATION_MESSAGE, Collections.singletonList(new ErrorDetail(field, ErrorResponse.INVALID_CODE, value))),
                    headers,
                    HttpStatus.UNPROCESSABLE_ENTITY);

        } else if (ex.getCause() instanceof JsonParseException) {
            JsonParseException jsonParseException = (JsonParseException) ex.getCause();
            JsonLocation jsonLocation = jsonParseException.getLocation();
            String parseErrorMessage = "Error parsing json at column:" + jsonLocation.getColumnNr() + " line:" + jsonLocation.getLineNr() + " offset:" + jsonLocation.getCharOffset();

            return new ResponseEntity<>(
                    new ErrorResponse(ErrorResponse.INPUT_PARSING_ERROR_CODE, parseErrorMessage, Collections.emptyList()),
                    headers,
                    status);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private static List<ErrorDetail> missingServletRequestParameterErrorToErrorDetails(String parameterName) {
        return Collections.singletonList(new ErrorDetail(parameterName, ErrorResponse.EMPTY_CODE, null));
    }

    private static List<ErrorDetail> bindingResultErrorsToErrorDetails(BindingResult bindingResult) {
        List<ErrorDetail> details = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            details.add(new ErrorDetail(fieldError.getField(), validationCodeToErrorDetailCode(fieldError.getCode()), fieldError.getRejectedValue()));
        }
        for (ObjectError objectError : bindingResult.getGlobalErrors()) {
            details.add(new ErrorDetail(objectError.getObjectName(), validationCodeToErrorDetailCode(objectError.getCode()), objectError.getDefaultMessage()));
        }
        return details;
    }

    private static String validationCodeToErrorDetailCode(String source) {
        String target = VALIDATION_CODE_MAPPINGS.get(source);
        if (target == null) {
            target = source;
        }
        return target;
    }

}
