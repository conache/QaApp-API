package com.project.qa.controller;

import com.project.qa.service.AdminService;
import com.project.qa.service.CompanyAdministratorService;
import com.project.qa.service.GroupService;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/company")
public class CompanyAdministratorController {

    public final AdminService adminService;
    public final GroupService groupService;
    public final CompanyAdministratorService companyAdministratorService;

    @Autowired
    public CompanyAdministratorController(AdminService adminService, GroupService groupService, CompanyAdministratorService companyAdministratorService) {
        this.adminService = adminService;
        this.groupService = groupService;
        this.companyAdministratorService = companyAdministratorService;
    }


    @PostMapping(path = "/add")
    public String addUser(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws HttpException {
        //return adminService.addUser(request, requestBody);
        return null;
    }

    @PostMapping(path = "/addGroup")
    public void addGroup(HttpServletRequest request, @RequestBody String name) {
        companyAdministratorService.addGroup(request, name);
    }

    @GetMapping(path = "/users")
    public PageImpl<UserRepresentation> findAllUsersByGroup(HttpServletRequest request, @RequestParam int page, @RequestParam int size) {
        return companyAdministratorService.findAllUsersByGroup(request, page, size);
    }
}
