package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminService {

    String addUser(HttpServletRequest request, CustomUser customUser) throws HttpException;

    List<GroupRepresentation> findGroups(HttpServletRequest request);

    void deleteGroupWithUsers(HttpServletRequest request, String groupId);
}
