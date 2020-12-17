package com.example.demo.service;

import com.example.demo.exceptions.CustomException;
import com.example.demo.predicates.FilteringPredicates;
import com.example.demo.predicates.SortingPredicates;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilterService {

    private static final Logger logger = LogManager.getLogger(FilterService.class);

    private List<JsonNode> objectList = new ArrayList<>();
    private List<JsonNode> filteredObjectList = new ArrayList<>();

    /*

     */
    public void addFilteredObjects(List<JsonNode> object) {
        this.filteredObjectList.addAll(object);
    }

    public Map<String, Map<String, Object>> buildConditionMap(Iterator<Map.Entry<String, JsonNode>> conditionNodes) {
        Map<String, Map<String, Object>> conditions = new HashMap<>();

        while (conditionNodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = conditionNodes.next();
            Map<String, Object> map = new HashMap<>();
            logger.info("key --> " + entry.getKey() + " value--> " + entry.getValue());
            for (final JsonNode objNode : entry.getValue()) {
                if (objNode.isValueNode()) {
                    map.put(objNode.textValue(), null);
                } else objNode.fields().forEachRemaining(e -> map.put(e.getKey(), e.getValue()));
            }

            conditions.put(entry.getKey(), map);
        }

        return conditions;
    }

    /*

     */
    private void buildObjectMap(JsonNode data) {
        if (data.isArray()) {
            for (final JsonNode objNode : data) {
                objectList.add(objNode);
            }
        } else throw new CustomException("Data is not a list");
    }

    /*
    TODO: make multiply filtering and sort possible and make filtering use 2 or 3 threads for parallel
     */
    private void filter(List<JsonNode> list, Map<String, Map<String, Object>> conditions, String cond, String key) {
        addFilteredObjects(list.stream().parallel()
                              .filter(FilteringPredicates.forName(cond)
                              .filter(key, conditions.get(cond).get(key)))
                              .collect(Collectors.toList()));
    }

    /*

     */
    public List<JsonNode> filterAndSort(JsonNode data, JsonNode condition) {
        Iterator<Map.Entry<String, JsonNode>> conditionNodes = condition.fields();

        Map<String, Map<String, Object>> conditions = buildConditionMap(conditionNodes);

        buildObjectMap(data);

        filterObjects(conditions, objectList);

        conditions.keySet().forEach(sortingCondition -> {
            if (SortingPredicates.forName(sortingCondition) != null) {
                conditions.get(sortingCondition).keySet().forEach(key -> {
                    if ((conditions.get(sortingCondition).get(key) == null) && (objectList.stream().noneMatch(t -> t.get(key) == null))) {
                        objectList.sort(SortingPredicates.forName(sortingCondition).compare(key));
                    } else throw new CustomException("Not in every data list field for sorting exists");
                });
            }
        });

        return objectList;
    }

    /*

     */
    private void filterObjects(Map<String, Map<String, Object>> conditions, List<JsonNode> objectList){

        conditions.keySet().forEach(filteringCondition -> {
            if (FilteringPredicates.forName(filteringCondition) != null) {
                conditions.get(filteringCondition).keySet().forEach(key -> {
                    if ((conditions.get(filteringCondition).get(key) != null) && (objectList.stream().noneMatch(t -> t.get(key) == null))) {
                        filter(objectList, conditions, filteringCondition, key);
                    } else throw new CustomException("Not in every data list field for filtering exists");
                });
            }
        });
    }

}

