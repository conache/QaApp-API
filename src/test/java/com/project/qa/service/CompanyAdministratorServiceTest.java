package com.project.qa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.config.KeycloakConfig;
import com.project.qa.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompanyAdministratorServiceTest {

    @Mock
    KeycloakConfig keycloakConfig;

    @Mock
    RoleService roleService;

    @Mock
    GroupService groupService;

    @Mock
    TagService tagService;

    @Mock
    HttpServletRequest request;

    @Mock
    RealmResource realmResource;

    @Mock
    UsersResource usersResource;

    @Mock
    UserResource userResource;

    @Mock
    Pageable pageable;

    @Mock
    UserServiceImpl userService;

    @Mock
    GroupResource groupResource;

    @Mock
    PageRequest pageRequest;


    @Spy
    @InjectMocks
    CompanyAdministratorServiceImpl companyAdministratorService;

    @Test
    public void testFindAllUsersByGroup() {
        GroupRepresentation groupRepresentation = mockGroupRepresentation();
        when(userService.findCurrentUserGroup(request)).thenReturn(groupRepresentation);
        Map<String, Object> expectedResponse = mockUsersByGroup();
        when(groupService.findAllGroupMembersPageable(request, groupRepresentation.getId(), pageRequest)).thenReturn(expectedResponse);
        Map<String, Object> response = companyAdministratorService.findAllUsersByGroup(request, pageRequest);
        Object totalCountObject = response.get("totalCount");
        Object listObject = response.get("users");
        ObjectMapper objectMapper = new ObjectMapper();
        assertNotNull(totalCountObject);
        assertNotNull(listObject);
        int totalCount1 = (Integer) totalCountObject;
        assertEquals(totalCount1, 2);
        List<UserRepresentation> listUsers = (List<UserRepresentation>) listObject;
        assertEquals(listUsers.size(), 2);
    }

    @Test
    public void testAddGroup() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        List<UserRepresentation> userRepresentationList = mockMembers();
        String groupName = "groupName";
        String groupId = "groupId";
        when(groupService.addGroup(request, groupName)).thenReturn(groupId);
        when(groupService.findGroupResourceById(request, groupId)).thenReturn(groupResource);
        doNothing().when(companyAdministratorService).addRolesToGroup(request, groupResource);

        when(userService.findUserResource(request)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(mockUserRepresentation());
        doNothing().when(userResource).update(userRepresentation);
        when(groupResource.members()).thenReturn(mockMembers());

        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().get(userRepresentation.getId())).thenReturn(userResource);

        UserResource userResource = keycloakConfig.getRealm(request).users().get(userRepresentation.getId());
        doNothing().when(userResource).joinGroup(groupId);

        companyAdministratorService.addGroup(request, groupName);
        verify(companyAdministratorService, times(1)).addGroup(request, groupName);
    }

    @Test
    public void testDeleteGroup() {
        String id = "id";
        doNothing().when(groupService).deleteGroupById(request, id);
        companyAdministratorService.deleteGroupById(request, id);
        verify(companyAdministratorService, times(1)).deleteGroupById(request, id);
    }

    @Test
    public void testFindUserById() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        when(userService.findUserById(request, userRepresentation.getId())).thenReturn(userRepresentation);
        UserRepresentation resultedUser = companyAdministratorService.findUserById(request, userRepresentation.getId());
        assertEquals(userRepresentation.getId(), resultedUser.getId());
    }

    @Test
    public void testDeleteUserFromGroup() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        GroupRepresentation groupRepresentation = mockGroupRepresentation();
        Response expectedResponse = mockResponse();
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);

        when(groupService.findGroupByName(request, getUserAttribute(userRepresentation, GROUP).get(0))).thenReturn(groupRepresentation);
        when(userService.deleteUser(request, userRepresentation.getId(), groupRepresentation.getId())).thenReturn(expectedResponse);
        Response resultResponse = companyAdministratorService.deleteUserFromGroup(request, userRepresentation.getId());
        assertEquals(expectedResponse, resultResponse);
    }

    @Test
    public void testFindProposedTags() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        Page<Tag> page = mockPageTags();
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        when(tagService.findAllByGroupIdAndActive(getUserAttribute(userRepresentation, GROUP).get(0), false, pageable)).thenReturn(page);
        Page<Tag> resultedTags = companyAdministratorService.findProposedTags(request, pageable);
        assertEquals(page.getTotalElements(), resultedTags.getTotalElements());
    }

    @Test
    public void testEditUser() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        when(userService.findUserResource(request, userRepresentation)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        doNothing().when(userService).editUser(request, userRepresentation);
        companyAdministratorService.editUser(request, userRepresentation);
        verify(companyAdministratorService, times(1)).editUser(request, userRepresentation);
    }

    @Test
    public void testEditTag() {
        Tag tag = new Tag();
        tag.setId(1);
        when(tagService.addTag(tag)).thenReturn(1);
        int tagId = companyAdministratorService.editTag(tag);
        assertEquals(1, tagId);
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag();
        tag.setId(1);
        UserRepresentation userRepresentation = mockUserRepresentation();
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        when(tagService.addTag(tag)).thenReturn(1);
        Integer tagId = companyAdministratorService.addTag(request, tag);
        assertEquals(new Integer(1), tagId);
    }

    @Test
    public void testDeclineTag() {
        Integer tagId = 1;
        doNothing().when(tagService).deleteTagById(tagId);
        Integer tagIdResult = companyAdministratorService.declineTag(request, tagId);
        assertEquals(tagId, tagIdResult);
    }

    public GroupRepresentation mockGroupRepresentation() {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName("groupName");
        groupRepresentation.setId("groupId");
        return groupRepresentation;
    }

    public Map<String, Object> mockUsersByGroup() {
        Map<String, Object> response = new HashMap<>();
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(mockUserRepresentation());
        userRepresentationList.add(mockUserRepresentation());
        response.put("totalCount", userRepresentationList.size());
        response.put("users", userRepresentationList);
        return response;
    }

    public UserRepresentation mockUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("id");
        userRepresentation.setEmail("testEmail");
        userRepresentation.setFirstName("Dani");
        userRepresentation.setLastName("Printul Banatului");
        userRepresentation.setUsername("username");

        userRepresentation.setRequiredActions(defaultRequiredActions);
        userRepresentation.setEnabled(true);

        addUserAttribute(userRepresentation, GROUP, singletonList("group"));
        addUserAttribute(userRepresentation, JOB, singletonList("job"));
        addUserAttribute(userRepresentation, CORRECT_ANSWERS, singletonList("0"));
        addUserAttribute(userRepresentation, ROLE, singletonList(ROLE_USER.name()));
        return userRepresentation;
    }

    public List<UserRepresentation> mockMembers() {
        List<UserRepresentation> userRepresentationList = new ArrayList<>();
        userRepresentationList.add(mockUserRepresentation());
        userRepresentationList.add(mockUserRepresentation());
        return userRepresentationList;
    }

    public Response mockResponse() {
        return Response.accepted().build();
    }

    public Page<Tag> mockPageTags() {
        List<Tag> tagList = mockTagList();
        return new PageImpl<>(tagList);
    }

    public List<Tag> mockTagList() {
        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setName("tag1");

        Tag tag2 = new Tag();
        tag2.setName("tag2");

        tagList.add(tag1);
        tagList.add(tag2);
        return tagList;
    }
}
