package com.rws.lt.lc.mtsampleaddon.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateResponse {
    private List<String> translations;
}
