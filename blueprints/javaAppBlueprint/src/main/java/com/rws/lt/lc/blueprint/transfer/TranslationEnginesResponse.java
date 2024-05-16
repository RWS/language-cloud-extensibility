package com.rws.lt.lc.blueprint.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class TranslationEnginesResponse {

    private List<TranslationEngine> items;
    private int itemCount;

}
