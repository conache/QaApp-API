package com.project.qa.service;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminService {

    String addUser(HttpServletRequest request,UserRepresentation user);

    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUser(HttpServletRequest request, String search);

    List<RoleRepresentation> findAllRoles(HttpServletRequest request);

    RoleRepresentation findRole(HttpServletRequest request, String roleName);
}
