package com.project.qa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.config.KeycloakConfig;
import com.project.qa.model.Tag;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import org.javatuples.Pair;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.project.qa.enums.Roles.ROLE_COMPANY_ADMINISTRATOR;
import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Service
public class CompanyAdministratorServiceImpl implements CompanyAdministratorService {

    private final KeycloakConfig keycloakConfig;
    private final ClientService clientService;
    private final UserService userService;
    private final GroupService groupService;
    private final RoleService roleService;
    private final TagService tagService;
    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CompanyAdministratorServiceImpl(KeycloakConfig keycloakConfig, ClientService clientService, UserService userService, GroupService groupService, RoleService roleService, TagService tagService, QuestionService questionService, ObjectMapper objectMapper) {
        this.keycloakConfig = keycloakConfig;
        this.clientService = clientService;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
        this.tagService = tagService;
        this.questionService = questionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> findAllUsersByGroup(HttpServletRequest request, PageRequest page) {
        GroupRepresentation groupRepresentation = userService.findCurrentUserGroup(request);
        return groupService.findAllGroupMembersPageable(request, groupRepresentation.getId(), page);
    }

    @Override
    public UserRepresentation findUserById(HttpServletRequest request, String userId) {
        return userService.findUserById(request, userId);
    }

    @Override
    public void addGroup(HttpServletRequest request, String groupName) {
        String groupId = groupService.addGroup(request, groupName);
        GroupResource group = groupService.findGroupResourceById(request, groupId);

        addRolesToGroup(request, group);

        UserResource userResource = userService.findUserResource(request);

        UserRepresentation currentUser = userResource.toRepresentation();
        addUserAttribute(currentUser, GROUP, singletonList(groupName));
        addUserAttribute(currentUser, ROLE, singletonList(ROLE_COMPANY_ADMINISTRATOR.name()));
        addUserAttribute(currentUser, CORRECT_ANSWERS, singletonList("0"));

        userResource.update(currentUser);

        group.members().add(currentUser);

        keycloakConfig.getRealm(request).users().get(currentUser.getId()).joinGroup(groupId);
    }

    private void addRolesToGroup(HttpServletRequest request, GroupResource group) {
        RoleRepresentation role = roleService.findRealmRoleByName(request, ROLE_USER.name());

        RoleMappingResource roleMappingResource = group.roles();
        roleMappingResource.realmLevel().add(singletonList(role));

        String clientId = clientService.findClientIdByName(request, keycloakConfig.getClient());
        RoleResource clientRole = keycloakConfig.getRealm(request).clients().get(clientId).roles().get(ROLE_USER.name());
        roleMappingResource.clientLevel(clientId).add(singletonList(clientRole.toRepresentation()));
    }

    @Override
    public void deleteGroupById(HttpServletRequest request, String id) {
        groupService.deleteGroupById(request, id);
    }

    @Override
    public void deleteGroupByName(HttpServletRequest request, String name) {
        groupService.deleteGroupByName(request, name);
    }

    @Override
    public Response deleteUserFromGroup(HttpServletRequest request, String userId) {
        UserRepresentation currentUser = userService.findCurrentUser(request);
        String group = getUserAttribute(currentUser, GROUP).get(0);
        GroupRepresentation groupByName = groupService.findGroupByName(request, group);
        return userService.deleteUser(request, userId, groupByName.getId());
    }


    @Override
    public void editUser(HttpServletRequest request, UserRepresentation userRepresentation) {
        String newRoleName = getUserAttribute(userRepresentation, ROLE).get(0);

        UserResource userResource = userService.findUserResource(request, userRepresentation);
        String oldRoleName = getUserAttribute(userRepresentation, ROLE).get(0);
        if (!newRoleName.equals(oldRoleName)) {
            roleService.setUserRole(request, userResource, newRoleName);
        }
        userService.editUser(request, userRepresentation);
    }

    @Override
    public void saveAllUsers(List<UserRepresentation> read) {
//        userService.addUser(re)
    }

    @Override
    public Page<Tag> findProposedTags(HttpServletRequest request, Pageable pageable) {
        UserRepresentation currentUser = userService.findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);
        return tagService.findAllByGroupIdAndActive(groups.get(0), false, pageable);
    }

    @Override
    public void deleteTag(HttpServletRequest request, Integer tagId) {
        Tag tag = tagService.findTagById(tagId);
        final String tagName = tag.getName();
        int pageStart = 1;
        int pageSize = 10000;
        int remainingQuestionsToUpdate;
        do {
            Pair<List<QuestionAsResponse>, Long> questionPair = questionService.filterAllGroupQuestions(request, PageRequest.of(pageStart, pageSize), singletonList(tagName), "questionPublishDate");
            remainingQuestionsToUpdate = questionPair.getSize();
            for (Question question : questionPair.getValue0()) {
                List<String> questionTags = question.getQuestionTags();
                questionTags.remove(tag.getName());
                question.setQuestionTags(questionTags);
                questionService.editQuestion(request, question, emptyList());
            }
        } while (remainingQuestionsToUpdate != 0);
        tagService.deleteTagById(tagId);
    }

    @Override
    public void editTag(Tag tag) {
        tagService.addTag(tag);
    }

    @Override
    public Integer addTag(HttpServletRequest request, Tag tag) {
        UserRepresentation currentUser = userService.findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);
        tag.setGroupName(groups.get(0));
        tag.setActive(true);
        return tagService.addTag(tag);
    }

    @Override
    public Integer acceptTag(HttpServletRequest request, Integer tagId) {
        tagService.acceptTag(tagId);
        questionService.appendTagToQuestion(tagId);

        return tagId;
    }

    public Integer declineTag(HttpServletRequest request, Integer tagId) {
        tagService.deleteTagById(tagId);
        return tagId;
    }
}
