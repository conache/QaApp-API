package com.project.qa.service;

import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.AnswerAsResponse;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.elasticsearch.ModelManager;
import org.elasticsearch.client.RestHighLevelClient;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService{

    private final ModelManager<Answer> answerManager;
    private final ModelManager<Question> questionManager;
    private final HttpServletRequest request;
    private final UserService userService;

    @Autowired
    public AnswerServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, HttpServletRequest request) {
        this.answerManager = new ModelManager<>(Answer::new, esClient);
        this.questionManager = new ModelManager<>(Question::new, esClient);
        this.userService = userService;
        this.request = request;
    }

    @Override
    public Pair<List<AnswerAsResponse>,Long> getAnswersForQuestion(String questionId, Pageable pageable, String sortBy) {

        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Pair<List<Answer>, Long> qResult =  answerManager.getAnswersForQuestion(questionId, pageSize, pageSize *(pageNumber - 1), sortBy);
        List<AnswerAsResponse> answers = new ArrayList<>();
        for (Answer answer: qResult.getValue0()) {
            VoteStatus status = VoteStatus.NoVote;
            if(answer.getUpVotes().contains(userRepresentation.getId()))
            {
                status = VoteStatus.UpVote;
            }
            if(answer.getDownVotes().contains(userRepresentation.getId()))
            {
                status = VoteStatus.DownVote;
            }
            answers.add(new AnswerAsResponse(answer, status));
        }
        return  new Pair<>(answers,qResult.getValue1());

    }

    @Override
    public String addAnswer(Answer answer) {

        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        answer.setUserId(userRepresentation.getId());
        answer.setUserName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        answer.setPublishDate(new Date());
        String answerId =  answerManager.index(answer);
        if(answerId != null)
        {
            Question q = questionManager.getByID(answer.getParentId());
            q.setNoAnswers(q.getNoAnswers() + 1);
            questionManager.update(q);
        }
        return  answerId;
    }

    @Override
    public void addVote(String answerId, String questionId, boolean isUpVote) {

        Answer answer = answerManager.getByID(answerId,questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        if(isUpVote)
        {
            answer.upVote(userRepresentation.getId());
        }
        else
        {
            answer.downVote(userRepresentation.getId());
        }
        answerManager.update(answer,questionId);
    }

    @Override
    public void updateAnswer(Answer answer) {

        Answer originalAnswer = answerManager.getByID(answer.getModelId(),answer.getParentId());
        answer.setDownVotes(originalAnswer.getDownVotes());
        answer.setUpVotes(originalAnswer.getUpVotes());
        answer.setScore(originalAnswer.getScore());
        answer.setUserName(originalAnswer.getUserName());
        answer.setUserId(originalAnswer.getUserId());
        answer.setPublishDate(DateTime.now().toDate());
        answer.setCorrectAnswer(originalAnswer.isCorrectAnswer());
        answerManager.update(answer,answer.getParentId());

    }

    @Override
    public void deleteAnswer(String answerId, String questionId) {
        answerManager.delete(answerId, questionId);
    }
}
