package com.example.demo.service;

import com.example.demo.exceptions.CustomException;
import com.example.demo.predicates.FilteringPredicates;
import com.example.demo.predicates.SortingsPredicate;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilterService {

    private static final Logger logger = LogManager.getLogger(FilterService.class);

    private List<JsonNode> list = new ArrayList<>();


    public void setList(List<JsonNode> list) {
        this.list = list;
    }

    public List<JsonNode> filterAndSort(JsonNode data, JsonNode condition) {
        Iterator<Map.Entry<String, JsonNode>> nodes = condition.fields();

        /*
        build map of conditions
         */

        Map<String, Map<String, Object>> conditions = new HashMap<>();

        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = nodes.next();
            Map<String, Object> map = new HashMap<>();
            logger.info("key --> " + entry.getKey() + " value-->" + entry.getValue());
            for (final JsonNode objNode : entry.getValue()) {
                if (objNode.isValueNode()) {
                    map.put(objNode.textValue(), null);
                }
                else objNode.fields().forEachRemaining(e -> map.put(e.getKey(), e.getValue()));
            }

            conditions.put(entry.getKey(), map);
        }

        /*
        creating list of objects
         */
        if (data.isArray()) {
            for (final JsonNode objNode : data) {
                list.add(objNode);
            }
        } else throw new CustomException("Data is not a list");

        conditions.keySet().forEach(fCond -> {
            if (FilteringPredicates.forName(fCond) != null) {
                conditions.get(fCond).keySet().forEach(key -> {
                    if ((conditions.get(fCond).get(key) != null) && (list.stream().noneMatch(t -> t.get(key) == null))) {
                        filter(list, conditions, fCond, key);
                    } else throw new CustomException("Not in every data list field for filtering exists");
                });
            }

        });

        conditions.keySet().forEach(sCond -> {
            if (SortingsPredicate.forName(sCond) != null) {
                conditions.get(sCond).keySet().forEach(key -> {
                    if ((conditions.get(sCond).get(key) == null) && (list.stream().noneMatch(t -> t.get(key) == null))) {
                        list.sort(SortingsPredicate.forName(sCond).compare(key));
                    } else throw new CustomException("Not in every data list field for sorting exists");
                });
            }
        });

        return list;
    }

    private void filter(List<JsonNode> list, Map<String, Map<String, Object>> conditions, String cond, String key) {
        setList(list.stream().filter(FilteringPredicates.forName(cond).filter(key, conditions.get(cond).get(key))).collect(Collectors.toList()));
    }
}

