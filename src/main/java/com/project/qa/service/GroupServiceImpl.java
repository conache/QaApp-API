package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.qa.utils.KeycloakUtils.getEntityId;
import static com.project.qa.utils.RoleUtils.DEFAULT_ROLES;
import static java.util.Collections.singletonMap;

@Service
public class GroupServiceImpl implements GroupService {

    private final KeycloakConfig keycloakConfig;
    private final RoleService roleService;
    private final ClientService clientService;

    @Autowired
    public GroupServiceImpl(KeycloakConfig keycloakConfig, RoleService roleService, ClientService clientService) {
        this.keycloakConfig = keycloakConfig;
        this.roleService = roleService;
        this.clientService = clientService;
    }

    private GroupsResource getGroupsResource(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).groups();
    }

    @Override
    public List<String> findGroupsNames(HttpServletRequest request) {
        return findGroups(request)
                .stream().map(GroupRepresentation::getName).collect(Collectors.toList());
    }

    @Override
    public List<GroupRepresentation> findGroups(HttpServletRequest request) {
        return getGroupsResource(request).groups();
    }

    @Override
    public GroupRepresentation findGroupByName(HttpServletRequest request, String name) {
        List<GroupRepresentation> groupRepresentations = findGroups(request);
        return groupRepresentations
                .stream()
                .filter(elem -> name.equals(elem.getName()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group " + name + " not found"));
    }

    public GroupResource getGroupResource(HttpServletRequest request, String name) {
        GroupRepresentation groupRepresentation = findGroupByName(request, name);

        //TODO...cred ca trebuie sa fac cu null object
//        return groupRepresentation.
        return null;
    }

    @Override
    public void deleteGroup(HttpServletRequest request, String name) {
        GroupRepresentation group = findGroupByName(request, name);
    }

    //    public GroupResource findGroupResourceByName(HttpServletRequest request, String name) {
//        GroupsResource groupsResource = getGroupsResource(request);
//
//    }

    @Override
    public List<UserRepresentation> findAllGroupMembers(HttpServletRequest request, String groupId) {
        return getGroupsResource(request).group(groupId).members();
    }

    @Override
    public List<UserRepresentation> findAllGroupMembersPageable(HttpServletRequest request, String groupId, PageRequest page) {
        return getGroupsResource(request).group(groupId).members(page.getPageNumber(), page.getPageSize());
    }

    @Override
    public String addGroup(HttpServletRequest request, String name) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(name);
        groupRepresentation.setRealmRoles(DEFAULT_ROLES);
        groupRepresentation.setClientRoles(singletonMap(keycloakConfig.getClient(), DEFAULT_ROLES));


        GroupsResource groupsResource = getGroupsResource(request);
        Response response = groupsResource.add(groupRepresentation);
        return getEntityId(response);

    }

    @Override
    public GroupResource findGroupResourceById(HttpServletRequest request, String groupId) {
        return getGroupsResource(request).group(groupId);
    }

    @Override
    public void deleteGroupById(HttpServletRequest request, String groupId) {
        keycloakConfig.getRealm(request).groups().group(groupId).remove();
    }

    @Override
    public void deleteGroupByName(HttpServletRequest request, String name) {
        GroupRepresentation group = findGroupByName(request, name);
        keycloakConfig.getRealm(request).groups().group(group.getId()).remove();
    }
}
