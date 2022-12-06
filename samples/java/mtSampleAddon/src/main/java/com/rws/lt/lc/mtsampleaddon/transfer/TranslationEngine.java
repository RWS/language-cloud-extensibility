package com.rws.lt.lc.mtsampleaddon.transfer;

import com.rws.lt.lc.mtsampleaddon.exception.ValidationException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@ToString
@Getter
@NoArgsConstructor
@Slf4j
public class TranslationEngine {

    private static final String ENGINE_ID_SEPARATOR = "_";

    private static final String DEFAULT_MODEL = "base";

    private String id;
    private String model;
    private String name;
    private String engineSourceLanguage;
    private String engineTargetLanguage;
    @Setter
    private String matchingSourceLanguage;
    @Setter
    private Set<String> matchingTargetLanguages;

    public TranslationEngine(String model, String engineSourceLanguage, String engineTargetLanguage) {
        this.model = model == null ? DEFAULT_MODEL : model;
        this.engineSourceLanguage = engineSourceLanguage;
        this.engineTargetLanguage = engineTargetLanguage;
        this.name = this.model;
        id = String.join(ENGINE_ID_SEPARATOR, engineSourceLanguage, engineTargetLanguage, this.model);
    }

    public TranslationEngine(String model, String engineSourceLanguage, String engineTargetLanguage, String matchingSourceLanguage, Set<String> matchingTargetLanguages) {
        this(model, engineSourceLanguage, engineTargetLanguage);
        this.matchingSourceLanguage = matchingSourceLanguage;
        this.matchingTargetLanguages = matchingTargetLanguages;
    }

    public static TranslationEngine toTranslationEngine(String engineId) throws ValidationException {
        if (StringUtils.isBlank(engineId)) {
            throw new ValidationException("EngineId " + engineId + " is not valid");
        }

        String[] parts = engineId.split(ENGINE_ID_SEPARATOR);
        if (parts.length != 3) {
            throw new ValidationException("EngineId " + engineId + " is not valid");
        }

        return new TranslationEngine(parts[2], parts[0], parts[1]);
    }
}
