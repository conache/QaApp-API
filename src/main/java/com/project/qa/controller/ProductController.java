package com.project.qa.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
class ProductController {

    @GetMapping(path = "/products")
    public String getProducts(Model model) {
        model.addAttribute("products", Arrays.asList("iPad", "iPhone", "iPod"));
        return "test";
    }

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "/";
    }
}