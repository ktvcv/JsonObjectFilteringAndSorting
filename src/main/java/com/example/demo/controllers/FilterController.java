package com.example.demo.controllers;

import com.example.demo.service.FilterService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Controller
@RequestMapping("/api")
public class FilterController {

    private final FilterService filterService;

    @Autowired
    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    /*
    TODO: to write try/catch
     */
    @PostMapping("/")
    public Object convert(@RequestBody MultipartFile multipartFile) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = multipartFile.getInputStream();

        JsonNode jsonNode = objectMapper.readTree(inputStream);

        Objects.requireNonNull(jsonNode, "Not json type object received");

        JsonNode data = jsonNode.get("data");
        Objects.requireNonNull(data, "Data node is null");

        JsonNode condition = jsonNode.get("condition");
        Objects.requireNonNull(condition, "Condition node is null");

        return filterService.filterAndSort(data, condition);

    }
}
