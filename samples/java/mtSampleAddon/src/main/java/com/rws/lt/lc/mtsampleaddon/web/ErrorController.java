package com.rws.lt.lc.mtsampleaddon.web;

import com.rws.lt.lc.mtsampleaddon.transfer.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/error")
@Slf4j
public class ErrorController extends AbstractErrorController {

    @Autowired
    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Object> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> errorAttributes = getErrorAttributes(request, true);
        Object body;
        switch (status) {
            case INTERNAL_SERVER_ERROR:
                body = new ErrorResponse(ErrorResponse.GENERIC_APPLICATION_ERROR_CODE, ErrorResponse.GENERIC_APPLICATION_MESSAGE, Collections.emptyList());
                LOGGER.error("Internal server error {}", errorAttributes);
                break;
            default:
                LOGGER.warn("Global error {}", errorAttributes);
                // No content body for servlet container errors
                body = null;
        }

        return new ResponseEntity<>(body, status);
    }


    @Override public String getErrorPath() {
        return "/error";
    }
}
