package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.model.CustomUser;
import com.project.qa.model.Tag;
import com.project.qa.utils.UserUtils;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final TagService tagService;

    @Autowired
    public UserServiceImpl(KeycloakConfig keycloakConfig, RoleService roleService, GroupService groupService, TagService tagService) {
        this.keycloakConfig = keycloakConfig;
        this.roleService = roleService;
        this.groupService = groupService;
        this.tagService = tagService;
    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).users().list();
    }

    @Override
    public UserRepresentation findCurrentUser(HttpServletRequest request) {
        String id = keycloakConfig.getCurrentUserId(request);
        return findUserById(request, id);
    }

    @Override
    public UserRepresentation findUserById(HttpServletRequest request, String userId) {
        return keycloakConfig.getRealm(request).users().get(userId).toRepresentation();
    }

    @Override
    public UserResource findUserResource(HttpServletRequest request) {
        String currentUserId = keycloakConfig.getCurrentUserId(request);
        return keycloakConfig.getRealm(request).users().get(currentUserId);
    }

    @Override
    public UserResource findUserResource(HttpServletRequest request, UserRepresentation userRepresentation) {
        return keycloakConfig.getRealm(request).users().get(userRepresentation.getId());
    }

    @Override
    public UserResource findUserResourceById(HttpServletRequest request, String userId) {
        return keycloakConfig.getRealm(request).users().get(userId);
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

        return roleService.findUserRealmRoles(userResource);
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
    public String addUser(HttpServletRequest request, CustomUser customUser, GroupRepresentation groupRepresentation) {
        UserRepresentation user = customUser.getUserRepresentation();
        user.setRequiredActions(defaultRequiredActions);
        user.setEnabled(true);

        addUserAttribute(user, GROUP, singletonList(groupRepresentation.getName()));
        addUserAttribute(user, ROLE, singletonList(customUser.getRoleName()));
        addUserAttribute(user, JOB, singletonList(customUser.getJobName()));

        UsersResource usersResource = keycloakConfig.getRealm(request).users();
        Response response = usersResource.create(user);
        if (response.getStatus() != CREATED.value()) {
            throw new ResponseStatusException(valueOf(response.getStatus()), response.getStatusInfo().getReasonPhrase());
        }

        String userId = getEntityId(response);
        UserResource userResource = usersResource.get(userId);
        roleService.setUserRole(request, userResource, customUser.getRoleName());
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
        keycloakConfig.getRealm(request).users().get(userRepresentation.getId()).update(userRepresentation);
    }

    @Override
    public Page<Tag> findActiveTagsPageable(HttpServletRequest request, Pageable pageable) {
        UserRepresentation currentUser = findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);
        return tagService.findAllByGroupIdAndActive(groups.get(0), true, pageable);

    }

    @Override
    public List<Tag> findActiveTags(HttpServletRequest request) {
        UserRepresentation currentUser = findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);
        return tagService.findAllByGroupIdAndActive(groups.get(0), true);

    }
    @Override
    public Integer addTag(HttpServletRequest request, Tag tag) {
        UserRepresentation currentUser = findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);
        tag.setGroupName(groups.get(0));
        tag.setActive(false);
        return tagService.addTag(tag);
    }
}
