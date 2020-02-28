package com.project.qa.service;

import com.project.qa.config.KeycloakConfig;
import com.project.qa.model.CustomUser;
import com.project.qa.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserServiceTest {

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

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void testFindCurrentUser() {
        String userId = "testId";
        when(keycloakConfig.getCurrentUserId(request)).thenReturn(userId);
        assertThat(keycloakConfig.getCurrentUserId(request), is(userId));

        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(userRepresentation).when(userService).findUserById(request, userId);

        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        assertThat(userService.findCurrentUser(request), is(userRepresentation));
    }

    @Test
    public void testFindUserById() {
        String userId = "id";
        UserRepresentation userRepresentation = mockUserRepresentation();

        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().get(userId)).thenReturn(userResource);
        when(keycloakConfig.getRealm(request).users().get(userId).toRepresentation()).thenReturn(userRepresentation);
        when(userService.findUserById(request, userId)).thenReturn(userRepresentation);
        assertNotNull(userService.findUserById(request, userId));
        assertEquals(userService.findUserById(request, userId).getId(), userId);
    }

    @Test
    public void testFindUserResource() {
        String userId = "id";
        when(keycloakConfig.getCurrentUserId(request)).thenReturn(userId);
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().get(userId)).thenReturn(userResource);

        when(userService.findUserResource(request)).thenReturn(userResource);

        assertNotNull(userService.findUserResource(request));
        assertThat(userService.findUserResource(request), is(userResource));
    }

    @Test
    public void testFindUserResourceById() {
        String userId = "id";
        when(keycloakConfig.getCurrentUserId(request)).thenReturn(userId);
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().get(userId)).thenReturn(userResource);

        when(userService.findUserResourceById(request, userId)).thenReturn(userResource);

        assertNotNull(userService.findUserResourceById(request, userId));
        assertThat(userService.findUserResourceById(request, userId), is(userResource));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindUserFailWithUnsupportedOperationException() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        List<UserRepresentation> list = Arrays.asList(userRepresentation, userRepresentation);
        String username = userRepresentation.getUsername();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().search(username, 0, 2)).thenReturn(list);
        when(userService.findUser(request, username)).thenThrow(UnsupportedOperationException.class);
    }

    @Test()
    public void testFindUser() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        List<UserRepresentation> list = Arrays.asList(userRepresentation);
        String username = userRepresentation.getUsername();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().search(username, 0, 2)).thenReturn(list);
        assertThat(userService.findUser(request, username), is(userRepresentation));
    }

    @Test
    public void testFindCurrentUserGroup() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(userRepresentation).when(userService).findCurrentUser(request);

        List<String> userAttribute = getUserAttribute(userRepresentation, GROUP);

        GroupRepresentation groupRepresentation = mock(GroupRepresentation.class);
        when(groupService.findGroupByName(request, userAttribute.get(0))).thenReturn(groupRepresentation);
        when(userService.findCurrentUserGroup(request)).thenReturn(groupRepresentation);
        assertThat(userService.findCurrentUserGroup(request), is(groupRepresentation));
    }

    @Test
    public void testDeleteUser() {
        String userId = "id";
        String groupId = "groupId";
        Response response = mockResponse();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(keycloakConfig.getRealm(request).users().get(userId)).thenReturn(userResource);
        doNothing().when(userResource).leaveGroup(groupId);
        when(usersResource.delete(userId)).thenReturn(response);
        assertThat(userService.deleteUser(request, userId, groupId), is(response));
    }

    @Test
    public void testAddUser() throws URISyntaxException {
        CustomUser customUser = mockCustomUser();
        GroupRepresentation groupRepresentation = mockGroupRepresentation();
        UserRepresentation userRepresentation = customUser.getUserRepresentation();
        Response response = Response.created(new URI("test/" + userRepresentation.getId())).build();
        when(keycloakConfig.getRealm(request)).thenReturn(realmResource);
        when(keycloakConfig.getRealm(request).users()).thenReturn(usersResource);
        when(usersResource.create(customUser.getUserRepresentation())).thenReturn(response);
        when(usersResource.get(userRepresentation.getId())).thenReturn(userResource);
        doNothing().when(roleService).setUserRole(request, userResource, customUser.getRoleName());
        doNothing().when(userResource).executeActionsEmail(defaultRequiredActions);
        doNothing().when(userResource).joinGroup(groupRepresentation.getId());

        String userId = userService.addUser(request, customUser, groupRepresentation);
        assertEquals(userId, userRepresentation.getId());
    }

    @Test
    public void testFindActiveTagsPageable() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        Page<Tag> tagPage = mockPageTags();
        List<String> groups = getUserAttribute(userRepresentation, GROUP);
        doReturn(userRepresentation).when(userService).findCurrentUser(request);
        when(tagService.findAllByGroupIdAndActive(groups.get(0), true, pageable)).thenReturn(tagPage);

        assertEquals(userService.findActiveTagsPageable(request, pageable).getTotalElements(), tagPage.getTotalElements());
    }

    @Test
    public void testFindActiveTags() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        List<String> groups = getUserAttribute(userRepresentation, GROUP);
        List<Tag> tags = mockTagList();
        doReturn(userRepresentation).when(userService).findCurrentUser(request);
        when(tagService.findAllByGroupIdAndActive(groups.get(0), true)).thenReturn(tags);
        assertEquals(userService.findActiveTags(request).size(), tags.size());

    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("tagName");

        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(userRepresentation).when(userService).findCurrentUser(request);
        when(tagService.addTag(tag)).thenReturn(tag.getId());
        assertEquals(userService.addTag(request, tag), tag.getId());
    }

    @Test
    public void testGetUserAnswerScore() {
        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(userRepresentation).when(userService).findUserById(request, userRepresentation.getId());
        assertEquals(userService.getUserAnswerScore(request, userRepresentation.getId()), 0);
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

        addUserAttribute(userRepresentation, GROUP, singletonList("role"));
        addUserAttribute(userRepresentation, JOB, singletonList("job"));
        addUserAttribute(userRepresentation, CORRECT_ANSWERS, singletonList("0"));
        addUserAttribute(userRepresentation, ROLE, singletonList(ROLE_USER.name()));
        return userRepresentation;
    }

    public Response mockResponse() {
        return Response.accepted().build();
    }

    public CustomUser mockCustomUser() {
        CustomUser customUser = new CustomUser();
        customUser.setUserRepresentation(mockUserRepresentation());
        customUser.setJobName("jobName");
        customUser.setRoleName("roleName");
        return customUser;
    }

    public GroupRepresentation mockGroupRepresentation() {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName("groupName");
        groupRepresentation.setId("groupId");
        return groupRepresentation;
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
