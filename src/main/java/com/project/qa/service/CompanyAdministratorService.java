package com.project.qa.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Map;

public interface CompanyAdministratorService {

    Map<String, Object> findAllUsersByGroup(HttpServletRequest request, PageRequest page);

    void addGroup(HttpServletRequest request, String name);

    void deleteGroupById(HttpServletRequest request, String name);

    void deleteGroupByName(HttpServletRequest request, String name);

    Response deleteUserFromGroup(HttpServletRequest request, String userId);

    UserRepresentation findUserById(HttpServletRequest request, String userId);

    void editUser(HttpServletRequest request, UserRepresentation userRepresentation);
}
