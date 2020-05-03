package com.adeptj.modules.data.sql2o;

public class NamedParam {

    private final String name;

    private final String value;

    public NamedParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
