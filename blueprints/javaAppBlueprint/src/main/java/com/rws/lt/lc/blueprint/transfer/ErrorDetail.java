package com.rws.lt.lc.blueprint.transfer;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorDetail {

    private String name;
    private String code;
    private Object value;

    public ErrorDetail() {
    }

    public ErrorDetail(String name, String code, Object value) {
        this.name = name;
        this.code = code;
        this.value = value;
    }
}
