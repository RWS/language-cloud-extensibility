package com.rws.lt.lc.mtsampleapp.service;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@Getter
@ToString
public class GoogleSupportedLanguages {

    private Set<String> sources;
    private Set<String> targets;

    public GoogleSupportedLanguages(Set<String> sources, Set<String> targets) {
        this.sources = Collections.unmodifiableSet(sources);
        this.targets = Collections.unmodifiableSet(targets);
    }
}
