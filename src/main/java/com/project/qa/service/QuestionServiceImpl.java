package com.project.qa.service;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.project.qa.utils.UserUtils.GROUP;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final ModelManager<Question> modelManager;
    private final UserService userService;
    private final HttpServletRequest request;

    @Autowired
    public QuestionServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, HttpServletRequest request) {
        this.modelManager = new ModelManager<>(Question::new, esClient);
        this.userService = userService;
        this.request = request;
    }

    @Override
    public Question findQuestionById(String questionId) {
        return modelManager.getByID(questionId);
    }

    @Override
    public void deleteQuestionById(String questionId) {
        modelManager.delete(questionId);
    }

    @Override
    public Page<Question> findAllGroupQuestions(Pageable pageable) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return new PageImpl<>(modelManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0)));
    }

    @Override
    public Page<Question> filterAllGroupQuestions(Pageable pageable, List<String> tags, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        if(tags != null && tags.size() != 0)
            return new PageImpl<>(modelManager.filterByField("questionTags", tags,pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy));

        return new PageImpl<>(modelManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy));
    }


    @Override
    public String addQuestion(Question question) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        question.setGroupName(userGroups.get(0));
        question.setQuestionAuthorId(userRepresentation.getId());
        question.setQuestionPublishDate(new Date());
        return modelManager.index(question);
    }
}
