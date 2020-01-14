package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
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

    private RolesResource getRolesResource(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).roles();
    }


    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).roles().list();
    }

    @Override
    public RoleRepresentation findRealmRoleByName(HttpServletRequest request, String roleName) {
        return getRolesResource(request).get(roleName).toRepresentation();
    }


    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, String roleName) {
        ClientRepresentation clientRep = clientService.findClientRepresentation(request, keycloakConfig.getClient());

        RoleRepresentation realmRole = findRealmRoleByName(request, roleName);
        storedUser.roles().realmLevel().add(singletonList(realmRole));

        removeClientUserRoles(storedUser, clientRep.getId());
        List<RoleRepresentation> clientRoles = storedUser.roles().clientLevel(clientRep.getId()).listAvailable();
        RoleRepresentation userRoleClient = getUserRoleForClient(roleName, clientRoles);
        storedUser.roles().clientLevel(clientRep.getId()).add(singletonList(userRoleClient));
    }

    private RoleRepresentation getUserRoleForClient(String roleName, List<RoleRepresentation> clientRoles) {
        return clientRoles
                .stream()
                .filter(el -> roleName.equals(el.getName()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User client role " + roleName + " not found"));
    }

    @Override
    public List<String> findUserRealmRoles(UserResource user) {
        List<RoleRepresentation> rolesRepresentations = user.roles().realmLevel().listEffective();

        return rolesRepresentations
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    public void removeClientUserRoles(UserResource userResource, String clientId) {
        RoleScopeResource roleScopeResource = userResource.roles().clientLevel(clientId);
        List<RoleRepresentation> clientRoles = roleScopeResource.listEffective();
        roleScopeResource.remove(clientRoles);
    }
}
