package com.rws.lt.lc.mtsampleaddon.transfer;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class TranslateRequest {
    @Valid
    @NotNull
    private List<String> contents;

    @Valid
    @NotNull
    private String engineId;
}
