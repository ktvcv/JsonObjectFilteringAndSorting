package com.example.demo.predicates;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SortingPredicates {
    sort_by {
        public Comparator<JsonNode> compare(String field) {
            return Comparator.comparing(j -> j.get(field).asText());
        }
    };

    private static final Map<String, SortingPredicates> nameToValueMap =
            new HashMap<>();

    static {
        for (SortingPredicates value : EnumSet.allOf(SortingPredicates.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static SortingPredicates forName(String name) {
        return nameToValueMap.get(name);
    }

    public static boolean ifExists(String name) {
        return nameToValueMap.get(name) != null;
    }

    public abstract Comparator<JsonNode> compare(String field);
}
