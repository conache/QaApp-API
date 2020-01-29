package com.project.qa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.Tag;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ProposedEditQuestion;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.UserUtils;
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
import java.util.Map;

import static com.project.qa.utils.UserUtils.GROUP;
import static com.project.qa.utils.UserUtils.getUserAttribute;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final ModelManager<Question> questionManager;
    private final ModelManager<Answer> answerManager;
    private final UserService userService;
    private final TagService tagService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public QuestionServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, TagService tagService) {
        this.questionManager = new ModelManager<>(Question::new, esClient);
        this.answerManager = new ModelManager<>(Answer::new, esClient);
        this.userService = userService;
        this.tagService = tagService;
    }

    @Override
    public QuestionAsResponse findQuestionById(HttpServletRequest request, String questionId) {
        Question question = questionManager.getByID(questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        VoteStatus status = VoteStatus.NoVote;
        if (question.getUpVotes().contains(userRepresentation.getId())) {
            status = VoteStatus.UpVote;
        }
        if (question.getDownVotes().contains(userRepresentation.getId())) {
            status = VoteStatus.DownVote;
        }
        return new QuestionAsResponse(question, status);
    }

    @Override
    public void deleteQuestionById(String questionId) {
        Question question = questionManager.getByID(questionId);
        questionManager.loadAnswers(question);

        //delete associated answers
        for (Answer answer : question.getQuestionsAnswers()) {
            answerManager.delete(answer.getModelId(), questionId);
        }

        questionManager.delete(questionId);
    }

    @Override
    public Pair<List<Question>, Long> findAllGroupQuestions(HttpServletRequest request, Pageable pageable) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return questionManager.getAll(pageSize, pageSize * (pageNumber - 1), userGroups.get(0));
    }

    @Override
    public Pair<List<Question>, Long> findCurrentUserQuestions(HttpServletRequest request, Pageable pageable, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return questionManager.findByField("questionAuthorId", userRepresentation.getId(), pageSize, pageSize * (pageNumber - 1), userGroups.get(0), sortBy);
    }

    @Override
    public Pair<List<Question>, Long> filterAllGroupQuestions(HttpServletRequest request, Pageable pageable, List<String> tags, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        if (!isEmpty(tags)) {
            return questionManager.filterByField("questionTags", tags, pageSize, pageSize * (pageNumber - 1), userGroups.get(0), sortBy);
        }
        return questionManager.getAll(pageSize, pageSize * (pageNumber - 1), userGroups.get(0), sortBy);
    }

    @Override
    public List<Question> search(HttpServletRequest request, String text, int maxSize) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);
        return questionManager.matchLikeThis("questionTitle", text, maxSize, 0, userGroups.get(0)).getValue0();
    }

    @Override
    public String addQuestion(HttpServletRequest request, Map<String, Object> questionRequest) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = UserUtils.getUserAttribute(userRepresentation, GROUP);

        Question question = objectMapper.convertValue(questionRequest, Question.class);
        question.setGroupName(userGroups.get(0));
        question.setQuestionAuthorId(userRepresentation.getId());
        question.setQuestionAuthorName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        question.setQuestionPublishDate(new Date());
        String questionId = questionManager.index(question);


        List<String> groups = getUserAttribute(userRepresentation, GROUP);
        if (questionRequest.get("proposedTags") != null) {
            List<String> proposedTags = (List<String>) questionRequest.get("proposedTags");
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
    public void voteQuestion(HttpServletRequest request, String questionId, boolean isUpVote) {
        Question question = questionManager.getByID(questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);

        if (isUpVote) {
            question.upVote(userRepresentation.getId());
        } else {
            question.downVote(userRepresentation.getId());
        }
        questionManager.update(question);
    }

    @Override
    public void editQuestion(Question question) {
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
        if (isEmpty(tags)) {
            tags = new ArrayList<>();
        }
        tags.add(tag.getName());
        question.setQuestionTags(tags);
        questionManager.update(question);
    }

    @Override
    public Pair<List<ProposedEditQuestion>, Long> findAllUserProposedQuestions(HttpServletRequest request, Pageable pageable) {
        UserRepresentation user = userService.findCurrentUser(request);

        return null;
    }
}
