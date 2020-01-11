package com.project.qa.controller;

import com.project.qa.service.AdminService;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class CompanyAdministratorController {

    public final AdminService adminService;

    @Autowired
    public CompanyAdministratorController(AdminService adminService) {
        this.adminService = adminService;
    }


    @PostMapping(path = "/add")
    public String addUser(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws HttpException {
        //return adminService.addUser(request, requestBody);
        return null;
    }
}
