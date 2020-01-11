package com.project.qa.service;

import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Service
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public AdminServiceImpl(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public String addUser(HttpServletRequest request, Map<String, Object> user) throws HttpException {
        //return userService.addUser(request, user);
        return null;
    }

    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, String role) {

    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return userService.findAllUsers(request);
    }

    @Override
    public UserRepresentation findUser(HttpServletRequest request, String search) {
        return userService.findUser(request,"bog");
    }

    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        return null;
    }

    @Override
    public RoleRepresentation findRole(HttpServletRequest request, String roleName) {
        return null;
    }

    @Override
    public List<String> findUserRoles(HttpServletRequest request, String username) {
        return null;
    }

    @Override
    public List<GroupRepresentation> findGroups(HttpServletRequest request) {
        return groupService.findGroups(request);
    }

    @Override
    public GroupRepresentation findGroup(HttpServletRequest request, String group) {
        return groupService.findGroupByName(request, group);
    }

}
