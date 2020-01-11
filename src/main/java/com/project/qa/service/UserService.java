package com.project.qa.service;

import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUser(HttpServletRequest request, String username);

    String addUser(HttpServletRequest request, UserRepresentation user) throws HttpException;

    List<String> findUserRoles(HttpServletRequest request, String username);

    void addUserGroup(HttpServletRequest request, UserResource storedUser, GroupRepresentation group);

    UserRepresentation findCurrentUser(HttpServletRequest request);
}
