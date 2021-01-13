package com.example.demo.controllers;

import com.example.demo.exceptions.CustomException;
import com.example.demo.service.FilterService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.Tika;
import org.apache.tomcat.jni.File;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

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

   @GetMapping("/")
   public String getPage(){

        return "main";
   }
    @PostMapping("/")
    public MultipartFile convert(@RequestBody MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new CustomException("There is not any file");

        Tika tika = new Tika();
        String detectedType = tika.detect(file.getBytes());
        System.out.println(detectedType);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try(InputStream inputStream = file.getInputStream();) {
            jsonNode = objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new CustomException("Error in reading file");
        }

        Objects.requireNonNull(jsonNode, "Not json type object received");

        JsonNode data = jsonNode.get(0);
        Objects.requireNonNull(data, "Data node is null");

        JsonNode condition = jsonNode.get(1);
        Objects.requireNonNull(condition, "Condition node is null");

        filterService.filterAndSort(data, condition);

        return null;

    }
}
