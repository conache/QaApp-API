package com.project.qa.service;

import com.project.qa.model.CustomUser;
import org.apache.http.HttpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AdminServiceTest {


    @InjectMocks
    AdminServiceImpl adminService;

    @Mock
    HttpServletRequest request;

    @Mock
    UserService userService;

    @Mock
    GroupService groupService;


    @Test
    public void testAddValidUser() throws HttpException {
        UserRepresentation currentUser = mockUserRepresentation();
        when(userService.findCurrentUser(request)).thenReturn(currentUser);

        assertThat(userService.findCurrentUser(request), is(notNullValue()));

        GroupRepresentation groupRepresentation = mock(GroupRepresentation.class);
        CustomUser customUser = mock(CustomUser.class);

        List<String> userAttribute = getUserAttribute(currentUser, GROUP);
        when(groupService.findGroupByName(request, userAttribute.get(0))).thenReturn(groupRepresentation);
        when(userService.addUser(request, customUser, groupRepresentation)).thenReturn(currentUser.getId());
        assertEquals(adminService.addUser(request, customUser), "id");
    }

    @Test
    public void testFindGroups() {
        List<GroupRepresentation> groupRepresentations = new ArrayList<>();
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentations.add(groupRepresentation);
        when(groupService.findGroups(request)).thenReturn(groupRepresentations);
        assertEquals(1, adminService.findGroups(request).size());
    }

    public UserRepresentation mockUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("id");
        userRepresentation.setEmail("testEmail");
        userRepresentation.setFirstName("testFirstName");
        userRepresentation.setLastName("testLastName");

        userRepresentation.setRequiredActions(defaultRequiredActions);
        userRepresentation.setEnabled(true);

        addUserAttribute(userRepresentation, GROUP, singletonList("role"));
        addUserAttribute(userRepresentation, JOB, singletonList("job"));
        addUserAttribute(userRepresentation, CORRECT_ANSWERS, singletonList("0"));
        addUserAttribute(userRepresentation, ROLE, singletonList(ROLE_USER.name()));
        return userRepresentation;
    }
}
