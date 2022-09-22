package com.adeptj.modules.restclient.core;

import java.util.HashMap;
import java.util.Map;

public class MultipartRequest {

    private Map<String, Part> parts;

    public void addPart(String key, Part part) {
        if (this.parts == null) {
            this.parts = new HashMap<>();
        }
        this.parts.put(key, part);
    }

    public Map<String, Part> getParts() {
        return parts;
    }
}
