package com.project.qa.service;

import org.keycloak.representations.idm.ClientRepresentation;

import javax.servlet.http.HttpServletRequest;

public interface ClientService {
    String findClientIdByName(HttpServletRequest request, String name);

    ClientRepresentation findClientRepresentation(HttpServletRequest request, String name);
}
