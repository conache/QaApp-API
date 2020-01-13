package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.enums.Roles;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_COMPANY_ADMINISTRATOR;
import static com.project.qa.utils.UserUtils.GROUP;
import static com.project.qa.utils.UserUtils.getUserAttribute;
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
    public List<UserRepresentation> findAllUsersByGroup(HttpServletRequest request, PageRequest page) {
        GroupRepresentation groupRepresentation = userService.findCurrentUserGroup(request);
        return groupService.findAllGroupMembersPageable(request, groupRepresentation.getId(), page);
    }

    @Override
    public void addGroup(HttpServletRequest request, String name) {
        String groupId = groupService.addGroup(request, name);
        GroupResource group = groupService.findGroupResourceById(request, groupId);

        RoleRepresentation role = roleService.findRoleByName(request, Roles.ROLE_USER.name());
        List<RoleRepresentation> roles = singletonList(role);
        String clientId = clientService.findClientIdByName(request, keycloakConfig.getClient());

        RoleMappingResource roleMappingResource = group.roles();
        roleMappingResource.realmLevel().add(roles);
        roleMappingResource.clientLevel(clientId).add(roles);

        UserRepresentation currentUser = userService.findCurrentUser(request);
        UserResource userResource = userService.findUserResource(request, currentUser);
        group.members().add(currentUser);

        RoleRepresentation roleRepresentation = roleService.findRoleByName(request, ROLE_COMPANY_ADMINISTRATOR.name());
        roleService.setUserRole(request, userResource, roleRepresentation);
        keycloakConfig.getRealm(request).users().get(currentUser.getId()).joinGroup(groupId);
    }

    @Override
    public void deleteGroupById(HttpServletRequest request, String id) {
        groupService.deleteGroupById(request, id);
    }

    @Override
    public void deleteGroupByName(HttpServletRequest request, String name) {
        groupService.deleteGroupByName(request, name);
    }

    @Override
    public Response deleteUserFromGroup(HttpServletRequest request, String userId) {
        UserRepresentation currentUser = userService.findCurrentUser(request);
        String group = getUserAttribute(currentUser, GROUP).get(0);
        GroupRepresentation groupByName = groupService.findGroupByName(request, group);
        return userService.deleteUser(request, userId, groupByName.getId());
    }

    @Override
    public UserRepresentation findUserById(HttpServletRequest request, String userId) {
        return userService.findUserById(request, userId);
    }
}
