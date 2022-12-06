package com.rws.lt.lc.mtsampleaddon.service;

import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEngine;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslationEnginesRequest;

import java.util.List;

public interface TranslationEnginesService {

    List<TranslationEngine> getTranslationEngines(String accountId, TranslationEnginesRequest request);
}
