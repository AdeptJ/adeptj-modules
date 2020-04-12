package com.adeptj.modules.data.jpa;

public class InParameter {

    private final String name;

    private final Object value;

    private final Class<?> type;

    public InParameter(String name, Object value, Class<?> type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getType() {
        return type;
    }
}
