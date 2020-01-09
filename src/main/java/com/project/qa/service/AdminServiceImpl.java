package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    private final KeycloakConfig keycloakConfig;

    @Autowired
    public AdminServiceImpl(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }

    @Override
    public String addUser(HttpServletRequest request, UserRepresentation user) {
        UsersResource userResource = keycloakConfig.getRealm(request).users();
        user.setUsername("test1");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("tom+tester1@tdlabs.local");
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
        user.setRealmRoles(Arrays.asList("ROLE_ADMIN"));

        // Create user (requires manage-users role)
        Response response = userResource.create(user);
        System.out.println("Repsonse: " + response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        if (userId != null) {
            userResource.get(userId).roles().realmLevel()
                    .add(Collections.singletonList(findRole(request, "ROLE_ADMIN")));
        }
        return userId;
    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return keycloakConfig.getRealm(request).users().list();
    }

    @Override
    public UserRepresentation findUser(HttpServletRequest request, String search) {
        return keycloakConfig.getRealm(request).users().search(search, 0, 1).get(0);
    }

    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        addUser(request, new UserRepresentation());
        return keycloakConfig.getRealm(request).roles().list();
    }

    @Override
    public RoleRepresentation findRole(HttpServletRequest request, String roleName) {
        return keycloakConfig.getRealm(request).roles().get(roleName).toRepresentation();
    }
}
