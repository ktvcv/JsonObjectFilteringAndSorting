package com.example.demo.controllers;

import com.example.demo.service.FilterService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FilterController {

    private final FilterService filterService;

    @Autowired
    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping("/filter")
    public Object convert(@RequestBody JsonNode jsonNode) {

        Assert.notNull(jsonNode, "Not json type object received");

        JsonNode data = jsonNode.get("data");
        Assert.notNull(data, "Data node is null");

        JsonNode condition = jsonNode.get("condition");
        Assert.notNull(condition, "Condition node is null");

        return filterService.filterAndSort(data, condition);

    }
}
