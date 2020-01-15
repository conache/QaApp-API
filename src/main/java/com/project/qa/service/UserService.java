package com.project.qa.service;

import com.project.qa.model.CustomUser;
import com.project.qa.model.Tag;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

public interface UserService {
    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUserById(HttpServletRequest request, String userId);


    UserResource findUserResource(HttpServletRequest request);

    UserResource findUserResource(HttpServletRequest request, UserRepresentation userRepresentation);

    UserResource findUserResourceById(HttpServletRequest request, String userId);

    UserRepresentation findUser(HttpServletRequest request, String username);

    List<String> findUserRoles(HttpServletRequest request, String username);

    void addUserGroup(HttpServletRequest request, UserResource storedUser, GroupRepresentation group);

    UserRepresentation findCurrentUser(HttpServletRequest request);

    GroupRepresentation findCurrentUserGroup(HttpServletRequest request);

    String getUserToken(HttpServletRequest request);

    Response deleteUser(HttpServletRequest request, String userId, String groupId);

    String addUser(HttpServletRequest request, CustomUser customUser, GroupRepresentation groupRepresentation) throws HttpException;

    void setUserGroup(HttpServletRequest request, String userId, String groupName);

    void editUser(HttpServletRequest request, UserRepresentation userRepresentation);

    Page<Tag> findActiveTagsPageable(HttpServletRequest request, Pageable pageable);

    Integer addTag(HttpServletRequest request, Tag tag);
}
