package com.example.demo.predicates;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public enum FilteringPredicates {
    include {
        public Predicate<JsonNode> filter(String field, Object value) {
            return j -> j.get(field).equals(value);
        }
    },
    excludes {
        public Predicate<JsonNode> filter(String field, Object value) {
            return j -> !j.get(field).equals(value);
        }
    };

    private static final Map<String, FilteringPredicates> nameToValueMap =
            new HashMap<>();

    static {
        for (FilteringPredicates value : EnumSet.allOf(FilteringPredicates.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static FilteringPredicates forName(String name) {
        return nameToValueMap.get(name);
    }

    public static boolean ifExists(String name) {
        return nameToValueMap.get(name) != null;
    }

    public abstract Predicate<JsonNode> filter(String field, Object value);
}
