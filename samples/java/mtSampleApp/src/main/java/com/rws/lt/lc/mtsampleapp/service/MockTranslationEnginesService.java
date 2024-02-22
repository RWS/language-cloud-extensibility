package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.mtsampleapp.transfer.TranslationEngine;
import com.rws.lt.lc.mtsampleapp.transfer.TranslationEnginesRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A mock implementation for {@link TranslationEnginesService} that can be used without the having to provide real configuration settings
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "mockExtension", name = "enabled", havingValue = "true")
public class MockTranslationEnginesService implements TranslationEnginesService {

    private static final String DEFAULT_MODEL = "nmt";

    /**
     * Gets the list of available translation engines
     * @param accountId the accountId from the context
     * @param request the translation engines request
     * @return A list of TranslationEngine response objects
     */
    @Override
    public List<TranslationEngine> getTranslationEngines(String accountId, TranslationEnginesRequest request) {
        String model = Optional.ofNullable(request.getModel()).orElse(DEFAULT_MODEL);
        return getTargetLanguageToEngineMap(accountId, model, request);
    }

    /**
     * Maps the requested languages to the response model. We return all the requested languages since this is just a mock for testing.
     * @param accountId the accountId from the context
     * @param model the model for supported languages
     * @param request the translation engines request
     * @return A list of translation engine for each requested target language.
     */
    private List<TranslationEngine> getTargetLanguageToEngineMap(String accountId, String model, TranslationEnginesRequest request) {
        String engineSourceLanguage = request.getSourceLanguage();
        Map<String, Set<String>> targetMatchingLanguages = request.getTargetLanguage().stream().collect(Collectors.toMap(Function.identity(), Set::of));

        List<TranslationEngine> enginesMap = targetMatchingLanguages.entrySet().stream()
                // map each matching target language to a translation engine response object,
                // where the key(e.g. "en") is the matching target language while the value is the set of target languages variants(e.g. ["en-US", "en-UK"])
                .map(e -> new TranslationEngine(model, engineSourceLanguage, e.getKey(), request.getSourceLanguage(), e.getValue()))
                .collect(Collectors.toList());

        LOGGER.debug("AccountId {} for model {} has the following engines {}", accountId, model, enginesMap);
        return enginesMap;
    }
}
