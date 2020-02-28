package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ClientServiceTest {

    @Mock
    HttpServletRequest request;

    @Mock
    KeycloakConfig keycloakConfig;

    @Mock
    RealmResource realmResource;

    @Mock
    ClientsResource clientsResource;

    @Spy
    @InjectMocks
    ClientServiceImpl clientService;

    @Test
    public void testFindClientIdByName() {
        List<ClientRepresentation> clientRepresentationList = mockClientRepresentation();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).clients()).thenReturn(clientsResource);
        when(keycloakConfig.getRealm(request).clients().findAll()).thenReturn(clientRepresentationList);
        String clientIdByName = clientService.findClientIdByName(request, "name");
        assertEquals("id", clientIdByName);

    }

    List<ClientRepresentation> mockClientRepresentation() {
        List<ClientRepresentation> clientRepresentations = new ArrayList<>();
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId("id");
        clientRepresentation.setName("name");
        clientRepresentation.setClientId("name");
        clientRepresentations.add(clientRepresentation);
        return clientRepresentations;
    }

}
