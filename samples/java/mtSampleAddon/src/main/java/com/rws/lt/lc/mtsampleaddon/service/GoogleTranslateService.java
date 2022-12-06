package com.rws.lt.lc.mtsampleaddon.service;

import com.google.cloud.translate.v3.*;
import com.rws.lt.lc.mtsampleaddon.domain.AccountSettings;
import com.rws.lt.lc.mtsampleaddon.exception.InvalidConfigurationException;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A real implementation for {@link TranslateService} that uses the Google MT provider to translate the content from Language Cloud
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "mockExtension", name = "enabled", havingValue = "false", matchIfMissing = true)
public class GoogleTranslateService implements TranslateService {

    // google translate characters limitation per request
    @Value("${params.google.max.request.characters:30000}")
    private long maxGoogleRequestCharacters = 30000;

    private final AccountSettingsService accountSettingsService;
    private final GoogleClientProvider clientProvider;

    public GoogleTranslateService(AccountSettingsService accountSettingsService, GoogleClientProvider clientProvider) {
        this.accountSettingsService = accountSettingsService;
        this.clientProvider = clientProvider;
    }

    /**
     * Translates the received contents on the translation request
     * @param accountId the accountId from the context
     * @param engineId the translation engine id(should be specific to the source and target language pairs)
     * @param htmls the contents coming from Language Cloud in html format
     * @return a list of translated content
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    @Override
    public List<String> translate(String accountId, String engineId, List<String> htmls) throws InvalidConfigurationException {
        LOGGER.debug("translate with google >> accountId: {} engineId: {}", accountId, engineId);
        TranslationEngine translationEngine = TranslationEngine.toTranslationEngine(engineId);

        return translate(accountId, translationEngine, htmls);
    }

    /**
     * Translates the html contents using the Google MT provider
     * @param accountId the accountId from the context
     * @param translationEngine the translation engine used to perform the translations
     * @param htmls the contents coming from Language Cloud in html format
     * @return a list of translated content
     * @throws InvalidConfigurationException when the account settings are invalid
     */
    private List<String> translate(String accountId, TranslationEngine translationEngine, List<String> htmls) throws InvalidConfigurationException {
        AccountSettings settings = accountSettingsService.getSettings(accountId);

        List<String> toTranslate = new ArrayList<>();
        List<String> translated = new ArrayList<>();

        // a counter to avoid exceeding the character limit
        int characterCount = 0;

        // instantiate a new translation service client with the account settings
        try (TranslationServiceClient client = clientProvider.getTranslationServiceClient(settings)) {
            // group as many htmls as possible without exceeding the limit before sending the request to Google
            for (String html : htmls) {
                int htmlSize = html.length();
                if (htmlSize > maxGoogleRequestCharacters) {
                    LOGGER.error("Can't translate html with size {} for accountId {} and engineId {}", htmlSize, accountId, translationEngine.getId());
                    continue;
                }
                // when the content exceeds the limit send it to translation
                if (characterCount + htmlSize > maxGoogleRequestCharacters) {
                    translated.addAll(translate(translationEngine, settings, toTranslate, client));
                    // reset
                    toTranslate.clear();
                    characterCount = 0;
                }
                toTranslate.add(html);
                characterCount += htmlSize;
            }
            // if there are left contents to translate perform a final request
            if (!CollectionUtils.isEmpty(toTranslate)) {
                translated.addAll(translate(translationEngine, settings, toTranslate, client));
            }
        }
        LOGGER.debug("Finished translate. Returning {} translations", translated.size());
        return translated;
    }

    /**
     * Translates and extracts the translated text
     * @param translationEngine the translation engine
     * @param settings the account settings
     * @param toTranslate the contents to translate
     * @param client the translation service client
     * @return a list of translated content
     */
    private List<String> translate(TranslationEngine translationEngine, AccountSettings settings, List<String> toTranslate, TranslationServiceClient client) {
        List<Translation> translations = translateWithGoogle(client, settings, translationEngine, toTranslate);
        return translations.stream().map(Translation::getTranslatedText).collect(Collectors.toList());
    }

    /**
     * Translate using the Google MT provider
     * @param client the translation service client
     * @param settings the account settings
     * @param engine the translation engine
     * @param contents the contents to translate
     * @return a list of translations
     */
    private List<Translation> translateWithGoogle(TranslationServiceClient client, AccountSettings settings, TranslationEngine engine, List<String> contents) {
        LOGGER.debug("translateWithGoogle >> contents: [{}], from: [{}], to: [{}], model: [{}] ", contents, engine.getEngineSourceLanguage(), engine.getEngineTargetLanguage(), engine.getModel());
        // prepare the request
        // https://cloud.google.com/translate/docs/advanced/translating-text-v3#translating_input_strings
        LocationName parent = LocationName.of(settings.getGoogleProjectId(), settings.getLocation());
        TranslateTextRequest.Builder builder = TranslateTextRequest.newBuilder()
                .setParent(parent.toString())
                .setMimeType(ContentType.TEXT_HTML.getMimeType())
                .setSourceLanguageCode(engine.getEngineSourceLanguage())
                .setTargetLanguageCode(engine.getEngineTargetLanguage())
                .addAllContents(contents);
        GoogleHtmlUtil.toGoogleModel(engine.getModel(), settings).ifPresent(builder::setModel);

        // perform the translate request
        TranslateTextResponse response = client.translateText(builder.build());

        return response.getTranslationsList();
    }

}
