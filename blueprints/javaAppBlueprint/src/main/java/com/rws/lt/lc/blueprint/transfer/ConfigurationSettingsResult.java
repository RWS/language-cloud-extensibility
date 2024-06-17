package com.rws.lt.lc.blueprint.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConfigurationSettingsResult {
    private List<ConfigurationValue> items;
    private int itemCount;

    public ConfigurationSettingsResult(List<ConfigurationValue> items) {
        setItems(items);
    }

    private void setItems(List<ConfigurationValue> items) {
        this.items = Collections.unmodifiableList(items);
        this.itemCount = this.items.size();
    }
}
