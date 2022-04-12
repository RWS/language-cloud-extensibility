package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.transfer.TranslateRequest;
import com.rws.lt.lc.blueprint.transfer.TranslateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/translate")
@Slf4j
public class TranslateController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TranslateResponse translate(@RequestBody @Valid TranslateRequest request) {
        // Endpoint used to receive and translate the contents from LC
        LOGGER.info("translate >>");
        LOGGER.info("text: {}", request);

        // TODO: replace the following line with your actual translation implementation
        List<String> translations = Collections.emptyList();

        return new TranslateResponse(translations);
    }
}
