package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new NotFoundException("Group " + name + " not found"));
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


}
