package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.GroupRepresentation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminService {

    String addUser(HttpServletRequest request, CustomUser customUser) throws HttpException;

    List<GroupRepresentation> findGroups(HttpServletRequest request);

}
