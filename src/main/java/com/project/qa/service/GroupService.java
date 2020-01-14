package com.project.qa.service;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface GroupService {
    List<GroupRepresentation> findGroups(HttpServletRequest request);

    GroupRepresentation findGroupByName(HttpServletRequest request, String groupName);

    List<UserRepresentation> findAllGroupMembers(HttpServletRequest request, String groupId);

    GroupResource findGroupResourceById(HttpServletRequest request, String groupId);

    Map<String, Object> findAllGroupMembersPageable(HttpServletRequest request, String groupId, PageRequest page);

    String addGroup(HttpServletRequest request, String name);

    void deleteGroupById(HttpServletRequest request, String groupId);

    void deleteGroupByName(HttpServletRequest request, String name);
}
