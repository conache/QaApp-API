package com.project.qa.service;

import org.keycloak.representations.idm.GroupRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GroupService {
    List<GroupRepresentation> findGroups(HttpServletRequest request);

    List<String> findGroupsNames(HttpServletRequest request);

    GroupRepresentation findGroupByName(HttpServletRequest request, String groupName);
}
