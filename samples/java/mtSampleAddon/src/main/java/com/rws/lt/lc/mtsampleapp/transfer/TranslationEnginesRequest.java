package com.rws.lt.lc.mtsampleapp.transfer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class TranslationEnginesRequest {

    private String model;
    @NotNull
    private String sourceLanguage;
    @NotNull
    private List<String> targetLanguage;
    private boolean includeGlossaries;
    private boolean exactMatch;
}
