package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.enums.Roles;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class CompanyAdministratorServiceImpl implements CompanyAdministratorService {

    private final KeycloakConfig keycloakConfig;
    private final ClientService clientService;
    private final UserService userService;
    private final GroupService groupService;
    private final RoleService roleService;

    @Autowired
    public CompanyAdministratorServiceImpl(KeycloakConfig keycloakConfig, ClientService clientService, UserService userService, GroupService groupService, RoleService roleService) {
        this.keycloakConfig = keycloakConfig;
        this.clientService = clientService;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
    }

    @Override
    public PageImpl<UserRepresentation> findAllUsersByGroup(HttpServletRequest request, int page, int size) {
        GroupRepresentation groupRepresentation = userService.findCurrentUserGroup(request);
        List<UserRepresentation> groupMembers = groupService.findAllGroupMembers(request, groupRepresentation.getId());

        return new PageImpl<>(groupMembers, PageRequest.of(page, size, Sort.Direction.ASC), groupMembers.size());
    }

    @Override
    public void addGroup(HttpServletRequest request, String name) {
        String groupId = groupService.addGroup(request, name);
        GroupResource group = groupService.findGroupResourceById(request, groupId);

        RoleRepresentation role = roleService.findRole(request, Roles.ROLE_USER.name());
        List<RoleRepresentation> roles = singletonList(role);
        String clientId = clientService.findClientIdByName(request, keycloakConfig.getClient());

        RoleMappingResource roleMappingResource = group.roles();
        roleMappingResource.realmLevel().add(roles);
        roleMappingResource.clientLevel(clientId).add(roles);

        UserRepresentation currentUser = userService.findCurrentUser(request);
        group.members().add(currentUser);

    }
}
