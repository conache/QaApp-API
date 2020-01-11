package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    private final KeycloakConfig keycloakConfig;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public AdminServiceImpl(KeycloakConfig keycloakConfig, UserService userService, GroupService groupService) {
        this.keycloakConfig = keycloakConfig;
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public String addUser(HttpServletRequest request, UserRepresentation user) throws HttpException {
        return userService.addUser(request, user);
    }

    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, String role) {

    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return null;
    }

    @Override
    public UserRepresentation findUser(HttpServletRequest request, String search) {
        return null;
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
    public List<String> findGroups(HttpServletRequest request) {
        groupService.findGroupByName(request,"asdas");
        return null;
    }


}
