package com.project.qa.controller;

import com.project.qa.model.Tag;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.service.QuestionService;
import com.project.qa.service.TagService;
import com.project.qa.service.UserService;
import org.javatuples.Pair;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    public final UserService userService;
    public final TagService tagService;
    public final QuestionService questionService;

    @Autowired
    public UserController(UserService userService, TagService tagService, QuestionService questionService) {
        this.userService = userService;
        this.tagService = tagService;
        this.questionService = questionService;
    }

    @GetMapping(path = "/currentUser")
    public UserRepresentation findCurrentUser(HttpServletRequest request) {
        return userService.findCurrentUser(request);
    }

    @GetMapping(path = "/currentUserGroup")
    public GroupRepresentation findCurrentUserGroup(HttpServletRequest request) {
        return userService.findCurrentUserGroup(request);
    }

    @GetMapping(path = "/token")
    public String getUserToken(HttpServletRequest request) {
        return userService.getUserToken(request);
    }


    @PostMapping("/addTag")
    public Integer addTag(HttpServletRequest request, @RequestBody Tag tag) {
        return userService.addTag(request, tag);
    }

    @GetMapping("/findTag")
    public Tag findTagById(@RequestParam Integer tagId) {
        return tagService.findTagById(tagId);
    }

    @GetMapping("/tags")
    public Page<Tag> findAllPageable(HttpServletRequest request, final Pageable pageable) {
        return userService.findActiveTagsPageable(request, pageable);
    }

    @GetMapping("/allTags")
    public List<Tag> findAllPageable(HttpServletRequest request) {
        return userService.findActiveTags(request);
    }

    @GetMapping("/questions")
    public Pair<List<QuestionAsResponse>, Long> userQuestions(HttpServletRequest request, Pageable page, @RequestParam(required = false, defaultValue = "questionPublishDate") String sortBy) {
        return questionService.findCurrentUserQuestions(request, page, sortBy);
    }

    @PostMapping("/subscribe")
    public void subscribeToTopic(HttpServletRequest request, @RequestParam String questionId) {
        questionService.subscribeToQuestion(request, questionId);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribeFromTopic(HttpServletRequest request, @RequestParam String questionId) {
        questionService.unsubscribeFromQuestion(request, questionId);
    }

/*    @GetMapping("/updateUsers")
    public String updateUsers(HttpServletRequest request) {
        userService.updateUserScore(request);
        return null;
    }*/

}
