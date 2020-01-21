package com.project.qa.service;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.javatuples.Pair;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.javatuples.Pair;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.project.qa.utils.UserUtils.GROUP;

@Service
public class AnswerServiceImpl implements AnswerService{

    private final ModelManager<Answer> modelManager;
    private final HttpServletRequest request;
    private final UserService userService;

    @Autowired
    public AnswerServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, HttpServletRequest request) {
        this.modelManager = new ModelManager<>(Answer::new, esClient);
        this.userService = userService;
        this.request = request;
    }

    @Override
    public Pair<List<Answer>,Long> getAnswersForQuestion(String questionId, Pageable pageable, String sortBy) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return modelManager.getAnswersForQuestion(questionId, pageSize, pageSize *(pageNumber - 1), sortBy);
    }

    @Override
    public String addAnswer(Answer answer) {

        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        answer.setUserId(userRepresentation.getId());
        answer.setUserName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        answer.setPublishDate(new Date());
        return modelManager.index(answer);
    }
}
