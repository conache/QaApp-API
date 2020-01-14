package com.project.qa.controller;

import com.project.qa.model.Tag;
import com.project.qa.service.TagService;
import com.project.qa.service.UserService;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    public final UserService userService;
    public final TagService tagService;

    @Autowired
    public UserController(UserService userService, TagService tagService) {
        this.userService = userService;
        this.tagService = tagService;
    }

    @GetMapping(path = "/currentUser")
    public UserRepresentation findCurrentUser(HttpServletRequest request) {
        return userService.findCurrentUser(request);
    }

    @GetMapping(path = "/currentUserGroup")
    public GroupRepresentation findCurrentUserGroup(HttpServletRequest request) {
        return userService.findCurrentUserGroup(request);
    }

    @GetMapping(path = "/token")
    public String getUserToken(HttpServletRequest request) {
        return userService.getUserToken(request);
    }

/*    @PutMapping("/edit")
    public void editUser(HttpServletRequest request, @RequestBody UserRepresentation userRepresentation) {
        userService.editUser(request, userRepresentation);
    }*/

    @PostMapping("/addTag")
    public Integer addTag(@RequestBody Tag tag) {
        return tagService.addTag(tag);
    }

    @GetMapping("/findTag")
    public Tag findTagById(@RequestParam Integer tagId){
        return tagService.findTagById(tagId);
    }
}
