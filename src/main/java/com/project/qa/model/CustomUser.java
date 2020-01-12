package com.project.qa.model;

import org.keycloak.representations.idm.UserRepresentation;

public class CustomUser {

    private UserRepresentation userRepresentation;
    private String role;
    private String group;

    public UserRepresentation getUserRepresentation() {
        return userRepresentation;
    }

    public void setUserRepresentation(UserRepresentation userRepresentation) {
        this.userRepresentation = userRepresentation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
