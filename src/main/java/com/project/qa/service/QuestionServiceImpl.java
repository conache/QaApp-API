package com.project.qa.service;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.project.qa.utils.UserUtils.GROUP;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final ModelManager<Question> modelManager;
    private final UserService userService;

    @Autowired
    public QuestionServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService) {
        this.modelManager = new ModelManager<>(Question::new, esClient);
        this.userService = userService;
    }

    @Override
    public Question findQuestionById(String questionId) {
        return modelManager.getByID(questionId);
    }

    @Override
    public void deleteQuestionById(String questionId) {

    }

    @Override
    public Page<Question> findAllGroupQuestions(HttpServletRequest request, Pageable pageable) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
//        modelManager.
        return null;
    }

}
