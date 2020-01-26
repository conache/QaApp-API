package com.project.qa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.Tag;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.repository.TagRepository;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
import com.sun.mail.util.QEncoderStream;
import org.javatuples.Pair;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.project.qa.utils.UserUtils.GROUP;
import static com.project.qa.utils.UserUtils.getUserAttribute;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final ModelManager<Question> questionManager;
    private final ModelManager<Answer> answerManager;
    private final UserService userService;
    private final HttpServletRequest request;
    private final TagService tagService;

    @Autowired
    public QuestionServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, HttpServletRequest request, TagService tagService) {
        this.questionManager = new ModelManager<>(Question::new, esClient);
        this.answerManager = new ModelManager<>(Answer::new, esClient);
        this.userService = userService;
        this.request = request;
        this.tagService = tagService;
    }

    @Override
    public QuestionAsResponse findQuestionById(String questionId) {
        Question question =  questionManager.getByID(questionId);
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
        Question question = questionManager.getByID(questionId);
        questionManager.loadAnswers(question);

        //delete associated answers
        for (Answer answer:question.getQuestionsAnswers())
        {
            answerManager.delete(answer.getModelId(),questionId);
        }

        questionManager.delete(questionId);
    }

    @Override
    public  Pair<List<Question>,Long> findAllGroupQuestions(Pageable pageable) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return questionManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0));

    }

    @Override
    public  Pair<List<Question>,Long> filterAllGroupQuestions(Pageable pageable, List<String> tags, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        if(tags != null && tags.size() != 0)
            return questionManager.filterByField("questionTags", tags,pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy);

        return  questionManager.getAll(pageSize, pageSize *(pageNumber - 1), userGroups.get(0), sortBy);
    }

    @Override
    public List<Question> search(String text, int maxSize) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        return questionManager.matchLikeThis("questionTitle", text, maxSize, 0, userGroups.get(0)).getValue0();
    }

    @Override
    public String addQuestion(Map<String, Object> questionRequest) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        ObjectMapper oMapper = new ObjectMapper();
        Question question = null;
        question = oMapper.convertValue(questionRequest, Question.class);
        question.setGroupName(userGroups.get(0));
        question.setQuestionAuthorId(userRepresentation.getId());
        question.setQuestionAuthorName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        question.setQuestionPublishDate(new Date());
        String questionId = questionManager.index(question);

        UserRepresentation currentUser = userService.findCurrentUser(request);
        List<String> groups = getUserAttribute(currentUser, GROUP);


        if(questionRequest.get("proposedTags") != null)
        {
            List<String> proposedTags = (List<String>)questionRequest.get("proposedTags");
            for (String tagText : proposedTags) {

                Tag tag = new Tag();
                tag.setActive(false);
                tag.setName(tagText);
                tag.setQuestionId(questionId);
                tag.setGroupName(groups.get(0));
                tagService.addTag(tag);
            }
        }



        return questionId;
    }

    @Override
    public void voteQuestion(String questionId, boolean isUpVote) {

        Question question = questionManager.getByID(questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        if(isUpVote)
        {
            question.upVote(userRepresentation.getId());
        }
        else
        {
            question.downVote(userRepresentation.getId());
        }
        questionManager.update(question);
    }

    @Override
    public void updateQuestion(Question question) {

        Question originalQuestion = questionManager.getByID(question.getModelId());
        question.setDownVotes(originalQuestion.getDownVotes());
        question.setUpVotes(originalQuestion.getUpVotes());
        question.setScore(originalQuestion.getScore());
        question.setQuestionAuthorName(originalQuestion.getQuestionAuthorName());
        question.setQuestionAuthorId(originalQuestion.getQuestionAuthorId());
        question.setQuestionPublishDate(DateTime.now().toDate());
        questionManager.update(question);
    }

    @Override
    public void appendTagToQuestion(Integer tagId) {

        Tag tag = tagService.findTagById(tagId);
        Question question = questionManager.getByID(tag.getQuestionId());
        List<String> tags = question.getQuestionTags();
        if(tags == null || question.getQuestionTags().size() == 0)
        {
            question.setQuestionTags(new ArrayList<>());
        }
        question.getQuestionTags().add(tag.getName());
        questionManager.update(question);
    }

}
