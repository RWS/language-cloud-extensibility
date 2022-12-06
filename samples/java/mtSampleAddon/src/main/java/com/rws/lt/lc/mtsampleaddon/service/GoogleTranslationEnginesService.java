package com.rws.lt.lc.mtsampleaddon.service;

import com.rws.lt.lc.mtsampleaddon.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEngine;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEnginesRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A real implementation for {@link TranslationEnginesService} that uses the Google MT provider to retrieve the translation engines
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "mockExtension", name = "enabled", havingValue = "false", matchIfMissing = true)
public class GoogleTranslationEnginesService implements TranslationEnginesService {

    private static final String LANGUAGE_SEPARATOR = "-";
    private final GoogleSupportedLanguagesService supportedLanguagesService;

    private static final String[] STANDARD_MODELS = new String[]{"nmt", "base"};
    private final Map<String, String> languageCloudModelMapping = new HashMap<>();

    @Autowired
    public GoogleTranslationEnginesService(GoogleSupportedLanguagesService supportedLanguagesService) {
        this.supportedLanguagesService = supportedLanguagesService;
        languageCloudModelMapping.put("neural", "nmt");
    }

    /**
     * Gets the list of available translation engines
     * @param accountId the accountId from the context
     * @param request the translation engines request
     * @return A list of TranslationEngine response objects
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    @Override
    public List<TranslationEngine> getTranslationEngines(String accountId, TranslationEnginesRequest request) throws InvalidConfigurationException {
        // get the corresponding Google model
        Optional<String> model = getModel(request);
        Map<String, TranslationEngine> engineMap;
        if (model.isPresent()) {
            engineMap = getTargetLanguageToEngineMap(accountId, model.get(), request, Collections.emptySet());
        } else {
            // if the model is not specified in request we get the languages for each standard model
            engineMap = new HashMap<>();
            for (String standardModel : STANDARD_MODELS) {
                // after the 1st iteration, engineMap.keySet() might contain values(existingTargets)
                engineMap.putAll(getTargetLanguageToEngineMap(accountId, standardModel, request, engineMap.keySet()));
            }
        }

        return new ArrayList<>(engineMap.values());
    }

    /**
     * Filters the engines(language pairs) retrieved from Google by the requested languages.
     * @param accountId the accountId from the context
     * @param model the model for supported languages
     * @param request the translation engines request
     * @param existingTargets a set of existing target languages to avoid duplicates
     * @return A map with the translation engine response objects grouped by the requested target languages.
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    private Map<String, TranslationEngine> getTargetLanguageToEngineMap(String accountId, String model, TranslationEnginesRequest request, Set<String> existingTargets) throws InvalidConfigurationException {
        boolean exactMatch = request.isExactMatch();
        // retrieve the supported languages from Google MT
        GoogleSupportedLanguages supportedLanguages = supportedLanguagesService.getSupportedLanguages(accountId, model);

        // extract the sourceLanguage from the available source languages taking into consideration the exactMatch boolean
        String engineSourceLanguage = getEngineLanguage(request.getSourceLanguage(), supportedLanguages.getSources(), exactMatch);
        // if no sourceLanguage was found among the supported languages return an empty map
        if (engineSourceLanguage == null) {
            return new HashMap<>();
        }

        // a map that stores the matching requested target languages by the matching target language
        // e.g. of key value pair: "en" -> [ "en-US", "en-UK" ]
        Map<String, Set<String>> targetMatchingLanguages = new HashMap<>();
        Set<String> availableTargets = supportedLanguages.getTargets();

        // iterate through the requested target languages
        for (String requestedLanguage : request.getTargetLanguage()) {
            // extract the targetLanguage from the available target languages taking into consideration the exactMatch boolean
            String targetEngineLanguage = getEngineLanguage(requestedLanguage, availableTargets, exactMatch);
            // check if a match was found and is not already present in the existingTargets
            if (targetEngineLanguage != null && (existingTargets == null || !existingTargets.contains(targetEngineLanguage))) {
                // add the matching requested language to the map
                Set<String> matchingLanguages = targetMatchingLanguages.computeIfAbsent(targetEngineLanguage, k -> new HashSet<>());
                matchingLanguages.add(requestedLanguage);
            }
        }

        Map<String, TranslationEngine> enginesMap = targetMatchingLanguages.entrySet().stream()
                // map each matching target language to a translation engine response object,
                // where the key(e.g. "en") is the matching target language while the value is the set of target languages variants(e.g. ["en-US", "en-UK"])
                .map(e -> new TranslationEngine(model, engineSourceLanguage, e.getKey(), request.getSourceLanguage(), e.getValue()))
                // put the response objects into a another map grouped by getEngineTargetLanguage(the key from the previous mapping)
                .collect(Collectors.toMap(TranslationEngine::getEngineTargetLanguage, Function.identity()));

        LOGGER.debug("AccountId {} for model {} has the following engines {}", accountId, model, enginesMap);
        return enginesMap;
    }

    /**
     * Maps the value of the requested model(neural) to the Google model(nmt).
     */
    private Optional<String> getModel(TranslationEnginesRequest request) {
        Optional<String> model = Optional.ofNullable(request.getModel());
        return model.map(m -> languageCloudModelMapping.getOrDefault(m.toLowerCase(), m));
    }

    /**
     * Searches the requested language in the available languages.
     * @param requestedLanguageCode the requested language code
     * @param availableLanguages the languages retrieved from Google
     * @param exactMatch indicates whether the language should match exactly the requested language
     */
    private String getEngineLanguage(String requestedLanguageCode, Set<String> availableLanguages, boolean exactMatch) {
        // check if the availableLanguages include the exact value of requestedLanguageCode
        String engineLanguage = availableLanguages.contains(requestedLanguageCode) ? requestedLanguageCode : null;

        // if not found and exactMatch was not requested we search by the language's short form
        if (engineLanguage == null && !exactMatch) {
            // get partial matches
            String shortLanguage = getShortLanguage(requestedLanguageCode);
            if (shortLanguage != null) {
                engineLanguage = availableLanguages.contains(shortLanguage) ? shortLanguage : null;
            }
        }
        return engineLanguage;
    }

    /**
     * Gets the short form of a language. E.g. en-UK -> en
     */
    private String getShortLanguage(String language) {
        int index = language == null ? -1 : language.indexOf(LANGUAGE_SEPARATOR);
        return index > 0 ? language.substring(0, index) : null;
    }
}
