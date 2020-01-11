package com.project.qa.controller;

import com.project.qa.service.UserService;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    public final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/currentUser")
    public UserRepresentation findCurrentUser(HttpServletRequest request) {
        return userService.findCurrentUser(request);
    }

    @GetMapping(path = "/currentUserGroup")
    public GroupRepresentation findCurrentUserGroup(HttpServletRequest request) {
        return userService.findCurrentUserGroup(request);
    }

}
