package com.adeptj.modules.data.sql2o;

public class ColumnMapping {

    private final String columnName;

    private final String propertyName;

    public ColumnMapping(String columnName, String propertyName) {
        this.columnName = columnName;
        this.propertyName = propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
