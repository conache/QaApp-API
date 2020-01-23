package com.project.qa.service;

import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
import org.javatuples.Pair;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
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
    public QuestionAsResponse findQuestionById(String questionId) {
        Question question =  modelManager.getByID(questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        VoteStatus status = VoteStatus.NoVote;
        if(question.getUpVotes().contains(userRepresentation.getId()))
        {
            status = VoteStatus.UpVote;
        }
        if(question.getDownVotes().contains(userRepresentation.getId()))
        {
            status = VoteStatus.DownVote;
        }
        return new QuestionAsResponse(question, status);
    }

    @Override
    public void deleteQuestionById(String questionId) {
        modelManager.delete(questionId);
    }

    @Override
    public  Pair<List<Question>,Long> findAllGroupQuestions(Pageable pageable) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return modelManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0));

    }

    @Override
    public  Pair<List<Question>,Long> filterAllGroupQuestions(Pageable pageable, List<String> tags, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        if(tags != null && tags.size() != 0)
            return modelManager.filterByField("questionTags", tags,pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy);

        return  modelManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy);
    }

    @Override
    public List<Question> search(String text, int maxSize) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        return modelManager.matchLikeThis("questionTitle", text, maxSize, 0, userGroups.get(0)).getValue0();
    }

    @Override
    public String addQuestion(Question question) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        question.setGroupName(userGroups.get(0));
        question.setQuestionAuthorId(userRepresentation.getId());
        question.setQuestionAuthorName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        question.setQuestionPublishDate(new Date());
        return modelManager.index(question);
    }

    @Override
    public void voteQuestion(String questionId, boolean isUpVote) {

        Question question = modelManager.getByID(questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        if(isUpVote)
        {
            question.upVote(userRepresentation.getId());
        }
        else
        {
            question.downVote(userRepresentation.getId());
        }
        modelManager.update(question);
    }

    @Override
    public void updateQuestion(Question question) {

        Question originalQuestion = modelManager.getByID(question.getModelId());
        question.setDownVotes(originalQuestion.getDownVotes());
        question.setUpVotes(originalQuestion.getUpVotes());
        question.setScore(originalQuestion.getScore());
        question.setQuestionAuthorName(originalQuestion.getQuestionAuthorName());
        question.setQuestionAuthorId(originalQuestion.getQuestionAuthorId());
        question.setQuestionPublishDate(DateTime.now().toDate());
        modelManager.update(question);
    }

}
