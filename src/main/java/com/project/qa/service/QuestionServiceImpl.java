package com.project.qa.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.Tag;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ProposedEditQuestion;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.repository.elasticsearch.ModelManager;
import org.elasticsearch.client.RestHighLevelClient;
import org.javatuples.Pair;
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
    private final ModelManager<ProposedEditQuestion> proposedQuestionManager;
    private final ModelManager<Answer> answerManager;
    private final UserService userService;
    private final TagService tagService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public QuestionServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, TagService tagService) {
        this.questionManager = new ModelManager<>(Question::new, esClient);
        this.answerManager = new ModelManager<>(Answer::new, esClient);
        this.proposedQuestionManager = new ModelManager<>(ProposedEditQuestion::new, esClient);
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
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return questionManager.getAll(pageSize, pageSize * (pageNumber - 1), userGroups.get(0));
    }

    @Override
    public Pair<List<Question>, Long> findCurrentUserQuestions(HttpServletRequest request, Pageable pageable, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        return questionManager.findByField("questionAuthorId", userRepresentation.getId(), pageSize, pageSize * (pageNumber - 1), userGroups.get(0), sortBy);
    }

    @Override
    public Pair<List<Question>, Long> filterAllGroupQuestions(HttpServletRequest request, Pageable pageable, List<String> tags, String sortBy) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);
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
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);
        return questionManager.matchLikeThis("questionTitle", text, maxSize, 0, userGroups.get(0)).getValue0();
    }

    @Override
    public String addQuestion(HttpServletRequest request, Map<String, Object> questionRequest) {
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);
        String groupName = userGroups.get(0);

        Question question = objectMapper.convertValue(questionRequest, Question.class);
        question.setGroupName(groupName);
        question.setQuestionAuthorId(userRepresentation.getId());
        question.setQuestionAuthorName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        question.setQuestionPublishDate(new Date());
        String questionId = questionManager.index(question);


        Object proposedTagsObject = questionRequest.get("proposedTags");
        List<String> proposedTags = proposedTagsObject == null ? new ArrayList<>() : objectMapper.convertValue(proposedTagsObject, new TypeReference<List<String>>() {
        });
        saveProposedTags(questionId, groupName, proposedTags);

        return questionId;
    }

    private void saveProposedTags(String questionId, String groupName, List<String> proposedTags) {
        for (String tagText : proposedTags) {
            Tag tag = new Tag();
            tag.setActive(false);
            tag.setName(tagText);
            tag.setQuestionId(questionId);
            tag.setGroupName(groupName);
            tagService.addTag(tag);
        }
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
    public void editQuestion(HttpServletRequest request, Map<String, Object> objectMap) {
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        Object questionObject = objectMap.get("question");
        Object proposedTagsObject = objectMap.get("proposedTags");
        Question question = questionObject == null ? new Question() : objectMapper.convertValue(questionObject, Question.class);
        List<String> proposedTags = proposedTagsObject == null ? new ArrayList<>() : objectMapper.convertValue(proposedTagsObject, new TypeReference<List<String>>() {
        });
        question.setQuestionPublishDate(new Date());
        questionManager.update(question);
        saveProposedTags(question.getModelId(), groupName, proposedTags);
    }

    @Override
    public void editQuestion(HttpServletRequest request, Question question, List<String> tags) {
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        question.setQuestionPublishDate(new Date());
        questionManager.update(question);
        saveProposedTags(question.getModelId(), groupName, tags);
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
    public Pair<List<ProposedEditQuestion>, Long> findAllUserProposedQuestions(HttpServletRequest request, Pageable pageable, String sortBy) {
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        return proposedQuestionManager.findByFieldAndExistField("questionAuthorId", user.getId(), "proposedAuthorId", pageSize, pageSize * (pageNumber - 1), groupName, sortBy);
    }

    @Override
    public String addProposedQuestion(HttpServletRequest request, Map<String, Object> objectMap) {
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        Object questionObject = objectMap.get("question");
        Object proposedTagsObject = objectMap.get("proposedTags");
        ProposedEditQuestion proposedEditQuestion = questionObject == null ? new ProposedEditQuestion() : objectMapper.convertValue(questionObject, ProposedEditQuestion.class);
        List<String> proposedTags = proposedTagsObject == null ? new ArrayList<>() : objectMapper.convertValue(proposedTagsObject, new TypeReference<List<String>>() {
        });
        Question storedQuestion = questionManager.getByID(proposedEditQuestion.getModelId());
        ProposedEditQuestion question = new ProposedEditQuestion(storedQuestion);
        question.setProposedAuthorId(user.getId());
        question.setProposedAuthorUsername(user.getUsername());
        question.setProposedDate(new Date());
        question.setQuestionText(proposedEditQuestion.getQuestionText());
        question.setParentQuestionId(storedQuestion.getModelId());
        String id = proposedQuestionManager.index(question);
        saveProposedTags(id, groupName, proposedTags);
        return id;
    }

    @Override
    public void deleteProposedEditQuestionById(HttpServletRequest request, String proposedQuestionId) {
        proposedQuestionManager.delete(proposedQuestionId);
    }

    @Override
    public void acceptProposedQuestion(HttpServletRequest request, String proposedQuestionId) {
        ProposedEditQuestion proposedEditQuestion = proposedQuestionManager.getByID(proposedQuestionId);
        Question question = questionManager.getByID(proposedEditQuestion.getParentQuestionId());
        question.setQuestionText(proposedEditQuestion.getQuestionText());
        String questionId = question.getModelId();
        tagService.deleteTagsByQuestionId(questionId);

        List<Tag> proposedQuestionTags = tagService.findAllByQuestionId(proposedQuestionId);
        for (Tag tag : proposedQuestionTags) {
            tag.setQuestionId(questionId);
            tagService.updateTag(tag);
        }
        proposedQuestionManager.delete(proposedQuestionId);
    }

    @Override
    public ProposedEditQuestion findProposedEditQuestion(HttpServletRequest request, String proposedQuestionId) {
        return proposedQuestionManager.getByID(proposedQuestionId);
    }
}
