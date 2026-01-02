package com.example.Controller;

import java.util.Map;

/**
 * Generic query object (can be expanded).
 */
public class QueryFetcher {
    private final Map<String, String> params;

    public QueryFetcher(Map<String, String> params) {
        this.params = params;
    }

    public String get(String key) {
        return params.get(key);
    }

    public Map<String, String> parameters() {
        return params;
    }
}