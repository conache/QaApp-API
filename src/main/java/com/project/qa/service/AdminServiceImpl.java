package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.project.qa.utils.UserUtils.GROUP;
import static com.project.qa.utils.UserUtils.getUserAttribute;


@Service
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public AdminServiceImpl(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public String addUser(HttpServletRequest request, CustomUser customUser) throws HttpException {
        UserRepresentation currentUser = userService.findCurrentUser(request);

        GroupRepresentation groupRepresentation = groupService.findGroupByName(request, getUserAttribute(currentUser, GROUP).get(0));
        return userService.addUser(request, customUser, groupRepresentation);
    }

    @Override
    public List<GroupRepresentation> findGroups(HttpServletRequest request) {
        return groupService.findGroups(request);
    }
}
