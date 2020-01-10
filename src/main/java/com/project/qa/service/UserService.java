package com.project.qa.service;

import org.apache.http.HttpException;
import org.keycloak.representations.idm.UserRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    List<UserRepresentation> findAllUsers(HttpServletRequest request);

    UserRepresentation findUser(HttpServletRequest request, String username);

    String addUser(HttpServletRequest request, UserRepresentation user) throws HttpException;

    List<String> getUserRoles(HttpServletRequest request, String username);
}
