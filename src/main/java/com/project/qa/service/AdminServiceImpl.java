package com.project.qa.service;

import com.project.qa.model.CustomUser;
import com.project.qa.utils.UserUtils;
import org.apache.http.HttpException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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
    private final RoleService roleService;

    @Autowired
    public AdminServiceImpl(UserService userService, GroupService groupService, RoleService roleService) {
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
    }

    @Override
    public String addUser(HttpServletRequest request, CustomUser customUser) throws HttpException {
        UserRepresentation currentUser = userService.findCurrentUser(request);

        GroupRepresentation groupRepresentation = groupService.findGroupByName(request, getUserAttribute(currentUser, GROUP).get(0));
        RoleRepresentation roleRepresentation = roleService.findRoleByName(request, customUser.getRoleName());
        return userService.addUser(request, customUser, groupRepresentation, roleRepresentation);
    }

    @Override
    public void setUserRole(HttpServletRequest request, UserResource storedUser, String role) {

    }

    @Override
    public List<UserRepresentation> findAllUsers(HttpServletRequest request) {
        return userService.findAllUsers(request);
    }

    @Override
    public UserRepresentation findUser(HttpServletRequest request, String search) {
        return userService.findUser(request, "bog");
    }

    @Override
    public List<RoleRepresentation> findAllRoles(HttpServletRequest request) {
        return null;
    }

    @Override
    public RoleRepresentation findRole(HttpServletRequest request, String roleName) {
        return null;
    }

    @Override
    public List<String> findUserRoles(HttpServletRequest request, String username) {
        return null;
    }

    @Override
    public List<GroupRepresentation> findGroups(HttpServletRequest request) {
        return groupService.findGroups(request);
    }

    @Override
    public GroupRepresentation findGroup(HttpServletRequest request, String group) {
        return groupService.findGroupByName(request, group);
    }

    @Override
    public void deleteGroupWithUsers(HttpServletRequest request, String groupId) {
        List<UserRepresentation> users = groupService.findAllGroupMembers(request, groupId);
        users.forEach(el -> userService.deleteUser(request, el.getId(), groupId));
        groupService.deleteGroupById(request, groupId);
    }
}
