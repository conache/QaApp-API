package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.project.qa.utils.UserUtils.GROUP;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RoleServiceTest {

    @Mock
    KeycloakConfig keycloakConfig;

    @Mock
    ClientService clientService;

    @Mock
    RealmResource realmResource;

    @Mock
    RolesResource rolesResource;

    @Mock
    HttpServletRequest request;

    @Mock
    UserResource userResource;

    @Mock
    RoleMappingResource roleMappingResource;

    @Mock
    RoleScopeResource roleScopeResource;

    @Mock
    RoleResource roleResource;

    @Spy
    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    public void testGetRolesResource() {
        String roleName = GROUP;
        RoleRepresentation roleRepresentation = mockRoleRepresentation();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).roles()).thenReturn(rolesResource);
        when(roleService.getRolesResource(request).get(roleName)).thenReturn(roleResource);
        when(roleService.getRolesResource(request).get(roleName).toRepresentation()).thenReturn(roleRepresentation);
        RoleRepresentation resultRoleRepresentation = roleService.findRealmRoleByName(request, roleName);
        assertThat(resultRoleRepresentation, is(roleRepresentation));
    }

    @Test
    public void testRemoveClientUserRoles() {
        String clientId = "clientId";
        List<RoleRepresentation> roleRepresentationList = mockRoleRepresentationList();
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(userResource.roles().clientLevel(clientId)).thenReturn(roleScopeResource);
        when(roleScopeResource.listEffective()).thenReturn(roleRepresentationList);
        doNothing().when(roleScopeResource).remove(roleRepresentationList);
        roleService.removeClientUserRoles(userResource, clientId);
        verify(roleService, times(1)).removeClientUserRoles(userResource, clientId);
    }



    public List<RoleRepresentation> mockRoleRepresentationList() {
        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId("id");
        roleRepresentation.setName("name");
        roleRepresentationList.add(roleRepresentation);
        return roleRepresentationList;
    }

    public RoleRepresentation mockRoleRepresentation() {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId("id");
        roleRepresentation.setName("name");
        return roleRepresentation;
    }

    public ClientRepresentation mockClientRepresentation() {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId("id");
        clientRepresentation.setName("name");
        clientRepresentation.setClientId("name");
        return clientRepresentation;
    }
}
