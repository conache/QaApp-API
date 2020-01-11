package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final KeycloakConfig keycloakConfig;

    @Autowired
    public RoleServiceImpl(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }

    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).roles().list();
    }

    @Override
    public RoleRepresentation findRole(HttpServletRequest request, String roleName) {
        return keycloakConfig.getRealm(request).roles().get(roleName).toRepresentation();
    }

    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, String role) {
        storedUser.roles().realmLevel()
                .add(Collections.singletonList(findRole(request, role)));
    }

    @Override
    public List<String> findUserRoles(UserResource user) {
        List<RoleRepresentation> rolesRepresentations = user.roles().realmLevel().listEffective();

        return rolesRepresentations
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }
}
