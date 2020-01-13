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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.qa.utils.KeycloakUtils.getEntityId;
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

    @Override
    public GroupResource getGroupResource(HttpServletRequest request, String id) {
        return getGroupsResource(request).group(id);
    }

    @Override
    public List<UserRepresentation> findAllGroupMembers(HttpServletRequest request, String groupId) {
        return getGroupsResource(request).group(groupId).members();
    }

    @Override
    public Map<String, Object> findAllGroupMembersPageable(HttpServletRequest request, String groupId, PageRequest page) {

        int firstIndex = page.getPageNumber() * page.getPageSize();
        int lastIndex = firstIndex + page.getPageSize() - 1;
        List<UserRepresentation> users = getGroupsResource(request).group(groupId).members();
        Map<String, Object> response = new HashMap<>();

        response.put("totalCount", users.size());
        response.put("users", users.subList(firstIndex, lastIndex));
        return response;
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
