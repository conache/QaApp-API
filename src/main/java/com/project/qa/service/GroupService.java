package com.project.qa.service;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GroupService {
    List<GroupRepresentation> findGroups(HttpServletRequest request);

    List<String> findGroupsNames(HttpServletRequest request);

    GroupRepresentation findGroupByName(HttpServletRequest request, String groupName);

    void deleteGroup(HttpServletRequest request, String name);

    List<UserRepresentation> findAllGroupMembers(HttpServletRequest request, String groupId);

    void addGroup(HttpServletRequest request, String name);
}
