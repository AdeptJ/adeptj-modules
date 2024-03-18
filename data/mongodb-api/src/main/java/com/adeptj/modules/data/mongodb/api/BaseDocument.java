package com.adeptj.modules.data.mongodb.api;

import org.mongojack.ObjectId;

public abstract class BaseDocument {

    private String _id; // NOSONAR

    @ObjectId
    public String get_id() { // NOSONAR
        return this._id;
    }

    public void set_id(String _id) { // NOSONAR
        this._id = _id;
    }
}