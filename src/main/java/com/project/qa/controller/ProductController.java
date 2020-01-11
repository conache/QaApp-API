package com.project.qa.controller;

import org.apache.http.HttpException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.InvalidPropertiesFormatException;
import java.util.Map;

@RestController
class ProductController {

    @GetMapping(path = "/products", produces = {"application/json"})
    public String getProducts() throws HttpException, InvalidPropertiesFormatException {
        throw new InvalidPropertiesFormatException("not ok");
    }

    @PostMapping(path = "/something")
    public void handle(@RequestBody Map<String,Object> body) {
        body.get("ana");

    }

}