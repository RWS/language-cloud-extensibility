package com.rws.lt.lc.mtsampleaddon.web;

import com.rws.lt.lc.mtsampleaddon.exception.ValidationException;
import com.rws.lt.lc.mtsampleaddon.service.TranslationEnginesService;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEngine;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEnginesRequest;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEnginesResponse;
import com.rws.lt.lc.mtsampleaddon.util.RequestLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/translation-engines")
@Slf4j
public class TranslationEnginesController {

    @Autowired
    private TranslationEnginesService translationEnginesService;

    @GetMapping
    public TranslationEnginesResponse getTranslationEngines(@Valid TranslationEnginesRequest request) {
        // Endpoint used to retrieve the available translation engines(language pairs)
        LOGGER.info("getTranslationEngines >> {}", request);

        return new TranslationEnginesResponse(translationEnginesService.getTranslationEngines(RequestLocalContext.getActiveAccountId(), request));
    }

    @GetMapping("/{id}")
    public TranslationEngine getTranslationEngine(@PathVariable String id) throws ValidationException {
        // Endpoint used to retrieve the translation engine(language pair) by id
        LOGGER.info("getTranslationEngine >> {}", id);

        // TODO: Replace the following line with your actual "GetEngine" method(implementation needed)
        return TranslationEngine.toTranslationEngine(id);
    }


}
