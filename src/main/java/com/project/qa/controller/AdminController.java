package com.project.qa.controller;

import com.project.qa.service.AdminService;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController()
@RequestMapping("/admin")
public class AdminController {

    public final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/add")
    public String addUser(HttpServletRequest request, @RequestParam UserRepresentation user) {
        return adminService.addUser(request, user);
    }

    @GetMapping(path = "/users")
    public List<UserRepresentation> users(HttpServletRequest request) {
        return adminService.findAllUsers(request);
    }

    @GetMapping(path = "/findUser")
    public UserRepresentation findUser(HttpServletRequest request, @RequestParam String search) {
        return adminService.findUser(request, search);
    }

    @GetMapping(path = "/roles")
    public List<RoleRepresentation> roles(HttpServletRequest request) {
        return adminService.findAllRoles(request);
    }


}
