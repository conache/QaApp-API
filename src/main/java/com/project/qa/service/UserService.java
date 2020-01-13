package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

public interface UserService {
    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUserById(HttpServletRequest request, String userId);

    UserResource findUserResource(HttpServletRequest request, String username);

    UserResource findUserResource(HttpServletRequest request, UserRepresentation userRepresentation);

    UserRepresentation findUser(HttpServletRequest request, String username);

    List<String> findUserRoles(HttpServletRequest request, String username);

    void addUserGroup(HttpServletRequest request, UserResource storedUser, GroupRepresentation group);

    UserRepresentation findCurrentUser(HttpServletRequest request);

    GroupRepresentation findCurrentUserGroup(HttpServletRequest request);

    String getUserToken(HttpServletRequest request);

    Response deleteUser(HttpServletRequest request, String userId, String groupId);

    String addUser(HttpServletRequest request, CustomUser customUser, GroupRepresentation groupRepresentation, RoleRepresentation roleRepresentation) throws HttpException;

    void setUserGroup(HttpServletRequest request, String userId, String groupName);

    void editUser(HttpServletRequest request, UserRepresentation userRepresentation);
}
