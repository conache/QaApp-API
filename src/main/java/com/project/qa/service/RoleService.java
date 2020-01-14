package com.project.qa.service;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RoleService {
    void setUserRole(HttpServletRequest request, UserResource storedUser, String roleName);

    List<RoleRepresentation> findAllRoles(HttpServletRequest request);

    RoleRepresentation findRealmRoleByName(HttpServletRequest request, String roleName);

    List<String> findUserRealmRoles(UserResource user);
}
