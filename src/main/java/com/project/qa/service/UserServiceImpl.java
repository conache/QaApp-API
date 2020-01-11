package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.defaultRequiredActions;
import static org.springframework.http.HttpStatus.CREATED;

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
    public String addUser(HttpServletRequest request, UserRepresentation user) throws HttpException {

        user.setRequiredActions(defaultRequiredActions);
        user.setEnabled(true);

       // user.setClientRoles(Arrays.asList(ROLE_USER.name()));

        UsersResource userResource = keycloakConfig.getRealm(request).users();
        Response response = userResource.create(user);
        if (response.getStatus() != CREATED.value()) {
            throw new HttpException(response.getStatusInfo().getReasonPhrase());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        UserResource storedUser = userResource.get(userId);
        roleService.setUserRole(request, storedUser, ROLE_USER.name());
        // setDefaultUserPassword(storedUser);

        return userId;
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
        List<String> userGroups = user.getGroups();
        if (userGroups.size() == 1) {
            return groupService.findGroupByName(request, userGroups.get(0));
        }
        return null;
    }

    @Override
    public String getUserToken(HttpServletRequest request) {
        return keycloakConfig.getUserToken(request);
    }
}
