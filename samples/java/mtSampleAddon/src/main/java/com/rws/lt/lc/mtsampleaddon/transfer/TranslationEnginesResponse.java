package com.rws.lt.lc.mtsampleaddon.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class TranslationEnginesResponse {

    private List<TranslationEngine> items;
    private int itemCount;

    public TranslationEnginesResponse(List<TranslationEngine> items) {
        this.items = items == null ? Collections.emptyList() : items;
        itemCount = this.items.size();
    }

}
