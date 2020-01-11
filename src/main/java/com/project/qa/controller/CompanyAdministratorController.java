package com.project.qa.controller;

import com.project.qa.service.AdminService;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CompanyAdministratorController {

    public final AdminService adminService;

    @Autowired
    public CompanyAdministratorController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping(path = "/add")
    public String addUser(HttpServletRequest request, @RequestParam UserRepresentation user) throws HttpException {
        return adminService.addUser(request, user);
    }
}
