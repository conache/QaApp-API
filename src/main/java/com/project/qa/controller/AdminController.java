package com.project.qa.controller;

import com.project.qa.service.AdminService;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


//    @GetMapping(path = "/users")
//    public List<UserRepresentation> users(HttpServletRequest request) {
//        return adminService.findAllUsers(request);
//    }

    @GetMapping(path = "/findUser")
    public UserRepresentation findUser(HttpServletRequest request, @RequestParam String username) {
        return adminService.findUser(request, username);
    }

    @GetMapping(path = "/roles")
    public List<RoleRepresentation> roles(HttpServletRequest request) {
        return adminService.findAllRoles(request);
    }

    @GetMapping(path = "/group")
    public GroupRepresentation findGroup(HttpServletRequest request, @RequestParam String groupName) {
        return adminService.findGroup(request, groupName);
    }

    @GetMapping(path = "/groups")
    public List<GroupRepresentation> findGroups(HttpServletRequest request) {
        return adminService.findGroups(request);
    }

}
