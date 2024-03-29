package com.project.qa.model;

import org.keycloak.representations.idm.UserRepresentation;

public class CustomUser {

    private UserRepresentation userRepresentation;
    private String roleName;
    private String jobName;

    public UserRepresentation getUserRepresentation() {
        return userRepresentation;
    }

    public void setUserRepresentation(UserRepresentation userRepresentation) {
        this.userRepresentation = userRepresentation;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
