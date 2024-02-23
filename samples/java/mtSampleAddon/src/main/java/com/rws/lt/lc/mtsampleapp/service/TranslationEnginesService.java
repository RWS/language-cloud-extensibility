package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.mtsampleapp.transfer.TranslationEngine;
import com.rws.lt.lc.mtsampleapp.transfer.TranslationEnginesRequest;

import java.util.List;

public interface TranslationEnginesService {

    List<TranslationEngine> getTranslationEngines(String accountId, TranslationEnginesRequest request);
}
