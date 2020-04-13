package com.adeptj.modules.data.jpa.query;

public class OutParameter {

    private final String name;

    private final Class<?> type;

    public OutParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
