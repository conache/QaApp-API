package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.model.CustomUser;
import com.project.qa.utils.UserUtils;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.project.qa.utils.KeycloakUtils.getEntityId;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class UserServiceImpl implements UserService {

    private final KeycloakConfig keycloakConfig;
    private final RoleService roleService;
    private final GroupService groupService;

    @Autowired
    public UserServiceImpl(KeycloakConfig keycloakConfig, RoleService roleService, GroupService groupService) {
        this.keycloakConfig = keycloakConfig;
        this.roleService = roleService;
        this.groupService = groupService;
    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).users().list();
    }

    @Override
    public UserRepresentation findCurrentUser(HttpServletRequest request) {
        String username = keycloakConfig.getCurrentUsername(request);
        return findUser(request, username);
    }

    @Override
    public UserRepresentation findUserById(HttpServletRequest request, String userId) {
        return keycloakConfig.getRealm(request).users().get(userId).toRepresentation();
    }

    @Override
    public UserResource findUserResource(HttpServletRequest request, String username) {
        UserRepresentation user = findUser(request, username);
        return keycloakConfig.getRealm(request).users().get(user.getId());
    }

    @Override
    public UserResource findUserResource(HttpServletRequest request, UserRepresentation userRepresentation) {
        return keycloakConfig.getRealm(request).users().get(userRepresentation.getId());
    }

    @Override
    public UserRepresentation findUser(HttpServletRequest request, String username) {
        List<UserRepresentation> users = keycloakConfig.getRealm(request).users().search(username, 0, 2);
        if (users.size() > 1) {
            throw new UnsupportedOperationException("User id " + username + " is not unique");
        }
        if (users.size() == 0) {
            throw new UnsupportedOperationException("User id " + username + " not found");
        }
        return users.get(0);
    }

    @Override
    public List<String> findUserRoles(HttpServletRequest request, String username) {
        UserRepresentation user = findUser(request, username);
        UserResource userResource = loadUser(request, user);

        return roleService.findUserRoles(userResource);
    }

    private UserResource loadUser(HttpServletRequest request, UserRepresentation user) {
        return keycloakConfig.getRealm(request).users().get(user.getId());
    }

    @Override
    public void addUserGroup(HttpServletRequest request, UserResource storedUser, GroupRepresentation group) {
        storedUser.groups().add(group);
    }



    /*private void setDefaultUserPassword(UserResource storedUser) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("12345");

        storedUser.resetPassword(passwordCred);
    }*/

    @Override
    public GroupRepresentation findCurrentUserGroup(HttpServletRequest request) {
        UserRepresentation user = findCurrentUser(request);

        List<String> userGroups = getUserAttribute(user, GROUP);
        if (userGroups.size() == 1) {
            return groupService.findGroupByName(request, userGroups.get(0));
        }
        throw new ResponseStatusException(NOT_FOUND, "User group not found");
    }

    @Override
    public String getUserToken(HttpServletRequest request) {
        return keycloakConfig.getUserToken(request);
    }

    @Override
    public Response deleteUser(HttpServletRequest request, String userId, String groupId) {
        UsersResource usersResource = keycloakConfig.getRealm(request).users();
        UserResource user = usersResource.get(userId);
        user.leaveGroup(groupId);
        return usersResource.delete(userId);
    }

    @Override
    public String addUser(HttpServletRequest request, CustomUser customUser, GroupRepresentation groupRepresentation, RoleRepresentation roleRepresentation) {
        UserRepresentation user = customUser.getUserRepresentation();
        user.setRequiredActions(defaultRequiredActions);
        user.setEnabled(true);

        addUserAttribute(user, GROUP, singletonList(groupRepresentation.getName()));
        addUserAttribute(user, ROLE, singletonList(roleRepresentation.getName()));
        addUserAttribute(user, JOB, singletonList(customUser.getJobName()));

        UsersResource usersResource = keycloakConfig.getRealm(request).users();
        Response response = usersResource.create(user);
        if (response.getStatus() != CREATED.value()) {
            throw new ResponseStatusException(valueOf(response.getStatus()), response.getStatusInfo().getReasonPhrase());
        }

        String userId = getEntityId(response);
        UserResource userResource = usersResource.get(userId);
        roleService.setUserRole(request, userResource, roleRepresentation);
        userResource.joinGroup(groupRepresentation.getId());

        return userId;
    }

    @Override
    public void setUserGroup(HttpServletRequest request, String userId, String groupName) {
        GroupRepresentation groupRepresentation = groupService.findGroupByName(request, groupName);

        UserResource userResource = keycloakConfig.getRealm(request).users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        addUserAttribute(userRepresentation, UserUtils.GROUP, singletonList(groupName));
        userResource.update(userRepresentation);

        List<GroupRepresentation> userGroups = userResource.groups();
        if (!isEmpty(userGroups)) {
            userGroups.forEach(el -> userResource.leaveGroup(el.getId()));
        }
        userResource.joinGroup(groupRepresentation.getId());
    }

    @Override
    public void editUser(HttpServletRequest request, UserRepresentation userRepresentation) {
        UserResource userResource = keycloakConfig.getRealm(request).users().get(userRepresentation.getId());
        userResource.update(userRepresentation);
    }
}