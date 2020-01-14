package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
public class RoleServiceImpl implements RoleService {

    private final KeycloakConfig keycloakConfig;
    private final ClientService clientService;

    @Autowired
    public RoleServiceImpl(KeycloakConfig keycloakConfig, ClientService clientService) {
        this.keycloakConfig = keycloakConfig;
        this.clientService = clientService;
    }

    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).roles().list();
    }

    @Override
    public RoleRepresentation findRoleByName(HttpServletRequest request, String roleName) {
        return keycloakConfig.getRealm(request).roles().get(roleName).toRepresentation();
    }

    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, RoleRepresentation roleByName) {
        storedUser.roles().realmLevel()
                .add(singletonList(roleByName));

        ClientRepresentation clientRep = clientService.findClientRepresentation(request, keycloakConfig.getClient());
        storedUser.roles().clientLevel(clientRep.getId()).add(singletonList(roleByName));
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
