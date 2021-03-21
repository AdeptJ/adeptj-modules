package com.adeptj.modules.data.mongodb.api;

import org.mongojack.ObjectId;

public abstract class BaseDocument {

    private String _id;

    @ObjectId
    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}