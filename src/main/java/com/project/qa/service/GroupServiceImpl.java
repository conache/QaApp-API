package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.utils.RoleUtils;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.qa.utils.RoleUtils.DEFAULT_ROLES;
import static java.util.Collections.singletonMap;

@Service
public class GroupServiceImpl implements GroupService {

    private final KeycloakConfig keycloakConfig;

    @Autowired
    public GroupServiceImpl(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
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
    public void addGroup(HttpServletRequest request, String name) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(name);
        groupRepresentation.setRealmRoles(DEFAULT_ROLES);
        groupRepresentation.setClientRoles(singletonMap(keycloakConfig.getClient(), DEFAULT_ROLES));

        getGroupsResource(request).add(groupRepresentation);
    }
}
