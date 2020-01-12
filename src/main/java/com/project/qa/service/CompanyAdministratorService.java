package com.project.qa.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.PageImpl;

import javax.servlet.http.HttpServletRequest;

public interface CompanyAdministratorService {

    PageImpl<UserRepresentation> findAllUsersByGroup(HttpServletRequest request, int page, int size);

    void addGroup(HttpServletRequest request, String name);
}
