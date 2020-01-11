package com.project.qa.controller;

import com.project.qa.service.AdminService;
import com.project.qa.service.GroupService;
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
    public final GroupService groupService;

    @Autowired
    public CompanyAdministratorController(AdminService adminService, GroupService groupService) {
        this.adminService = adminService;
        this.groupService = groupService;
    }


    @PostMapping(path = "/add")
    public String addUser(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws HttpException {
        //return adminService.addUser(request, requestBody);
        return null;
    }

    @PostMapping(path = "/addGroup")
    public void addGroup(HttpServletRequest request, @RequestBody String name) {
        groupService.addGroup(request, name);
    }
}
