package com.project.qa.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class KeycloakConfig {

    private final KeycloakCredentials credentials;

    @Autowired
    public KeycloakConfig(KeycloakCredentials credentials) {
        this.credentials = credentials;
    }

    public RealmResource getRealm(HttpServletRequest request) {
        Keycloak keycloak = getKeycloak(request);
        return keycloak.realm(credentials.getRealm());
    }

    private Keycloak getKeycloak(HttpServletRequest request) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return KeycloakBuilder.builder()
                .serverUrl(credentials.getAuthServerUrl())
                .realm(credentials.getRealm())
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }

    public String getCurrentUserId(HttpServletRequest request) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return context.getToken().getSubject();
    }

    public String getUserToken(HttpServletRequest request) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return context.getTokenString();
    }

    public String getClient() {
        return credentials.getResource();
    }
}
