package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminService {

    String addUser(HttpServletRequest request, CustomUser customUser) throws HttpException;

    void setUserRole(HttpServletRequest request, UserResource storedUser, String role);

    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUser(HttpServletRequest request, String search);

    List<RoleRepresentation> findAllRoles(HttpServletRequest request);

    RoleRepresentation findRole(HttpServletRequest request, String roleName);

    List<String> findUserRoles(HttpServletRequest request, String username);

    List<GroupRepresentation> findGroups(HttpServletRequest request);

    GroupRepresentation findGroup(HttpServletRequest request, String group);
}
