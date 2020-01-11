package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final KeycloakConfig keycloakConfig;

    @Autowired
    public ClientServiceImpl(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }

    @Override
    public String findClientIdByName(HttpServletRequest request, String name) {
        List<ClientRepresentation> clientRepresentations = keycloakConfig.getRealm(request).clients().findAll();
        ClientRepresentation clientRepresentation = clientRepresentations
                .stream()
                .filter(el -> name.equals(el.getClientId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client " + name + " not found"));

        return clientRepresentation.getId();
    }
}
