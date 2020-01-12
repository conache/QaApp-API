package com.project.qa.service;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RoleService {
    void setUserRole(HttpServletRequest request, UserResource storedUser, String role);

    List<RoleRepresentation> findAllRoles(HttpServletRequest request);

    RoleRepresentation findRoleByName(HttpServletRequest request, String roleName);

    List<String> findUserRoles(UserResource user);
}
