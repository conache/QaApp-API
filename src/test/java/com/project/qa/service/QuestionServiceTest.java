package com.project.qa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ProposedEditQuestion;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.repository.QuestionSubscribeRepository;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.EncryptUtils;
import org.javatuples.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuestionServiceTest {

    @Mock
    ModelManager<Question> questionManager;

    @Mock
    ModelManager<ProposedEditQuestion> proposedQuestionManager;

    @Mock
    ModelManager<Answer> answerManager;

    @Mock
    UserService userService;

    @Mock
    TagService tagService;

    @Mock
    QuestionSubscribeRepository questionSubscribeRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    PublishNotification publishNotification;

    @Mock
    HttpServletRequest request;

    @Mock
    Pageable pageable;

    @Spy
    @InjectMocks
    QuestionServiceImpl questionService;

    @Test
    public void testFindQuestionById() {
        questionService.setQuestionManager(questionManager);
        String questionId = "id";
        Question question = mockQuestion();
        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(question).when(questionManager).getByID(questionId);
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        when(userService.getUserAnswerScore(request, question.getQuestionAuthorId())).thenReturn(1);
        when(questionSubscribeRepository.isCurrentUserSubscribedToQuestion(question.getModelId(), userRepresentation.getEmail())).thenReturn(true);
        QuestionAsResponse questionAsResponse = questionService.findQuestionById(request, questionId);
        assertEquals(1, questionAsResponse.getUserScore());
    }

    @Test
    public void testDeleteQuestionById() {
        questionService.setQuestionManager(questionManager);
        questionService.setAnswerManager(answerManager);
        String questionId = "id";
        Question question = mockQuestion();
        UserRepresentation userRepresentation = mockUserRepresentation();
        doReturn(question).when(questionManager).getByID(questionId);
        doNothing().when(questionManager).loadAnswers(question);
        doNothing().when(answerManager).delete("id", questionId);
        doNothing().when(questionManager).delete(questionId);
        doNothing().when(questionSubscribeRepository).deleteAllByQuestionId(questionId);
        questionService.deleteQuestionById(questionId);
        verify(questionService, times(1)).deleteQuestionById(questionId);
    }

    @Test
    public void testFindAllGroupQuestions() {
        questionService.setQuestionManager(questionManager);
        UserRepresentation userRepresentation = mockUserRepresentation();
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);

        List<QuestionAsResponse> questionAsResponseList = mockQuestionAsResponse();
        Pair<List<Question>, Long> mockRes = mockResult();
        when(questionManager.getAll(pageable.getPageSize(), pageable.getPageSize() * (pageable.getPageNumber() - 1), userGroups.get(0))).thenReturn(mockRes);
        doReturn(questionAsResponseList).when(questionService).getDecryptedQuestionsAsResponse(request, userRepresentation.getEmail(), userGroups, mockRes);
        Pair<List<QuestionAsResponse>, Long> allGroupQuestions = questionService.findAllGroupQuestions(request, pageable);
        assertEquals(2, allGroupQuestions.getSize());
    }

    @Test
    public void testFindCurrentUserQuestions() {
        String sort = "sort";
        questionService.setQuestionManager(questionManager);
        UserRepresentation userRepresentation = mockUserRepresentation();
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        List<String> userGroups = getUserAttribute(userRepresentation, GROUP);

        List<QuestionAsResponse> questionAsResponseList = mockQuestionAsResponse();
        Pair<List<Question>, Long> mockRes = mockResult();
        when(questionManager.findByField("questionAuthorId", userRepresentation.getId(), pageable.getPageSize(), pageable.getPageSize() * (pageable.getPageNumber() - 1), userGroups.get(0), sort)).thenReturn(mockRes);
        doReturn(questionAsResponseList).when(questionService).getDecryptedQuestionsAsResponse(request, userRepresentation.getEmail(), userGroups, mockRes);
        Pair<List<QuestionAsResponse>, Long> allGroupQuestions = questionService.findCurrentUserQuestions(request, pageable,"sort");
        assertEquals(2, allGroupQuestions.getSize());

    }

    private List<QuestionAsResponse> mockQuestionAsResponse() {
        List<QuestionAsResponse> questionAsResponseList = new ArrayList<>();
        QuestionAsResponse questionAsResponse = new QuestionAsResponse(mockQuestion(), VoteStatus.DownVote, 1, true);
        questionAsResponseList.add(questionAsResponse);
        questionAsResponseList.add(questionAsResponse);
        return questionAsResponseList;
    }

    private Pair<List<Question>, Long> mockResult() {
        List<Question> questions = Arrays.asList(mockQuestion(), mockQuestion());
        return Pair.with(questions, 2L);
    }

    private Question mockQuestion() {
        Question question = new Question();
        question.setModelId("id");
        question.setQuestionTitle("title");
        question.setUpVotes(new ArrayList<>());
        question.setDownVotes(Arrays.asList(mockUserRepresentation().getId()));
        question.setGroupName("group");
        question.setQuestionAuthorId("authorId");
        question.setQuestionsAnswers(mockQuestionAnswer());
        question.setQuestionText(EncryptUtils.encrypt("text", question.getGroupName()));
        return question;
    }

    public UserRepresentation mockUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("id");
        userRepresentation.setEmail("testEmail");
        userRepresentation.setFirstName("testFirstName");
        userRepresentation.setLastName("testLastName");

        userRepresentation.setRequiredActions(defaultRequiredActions);
        userRepresentation.setEnabled(true);

        addUserAttribute(userRepresentation, GROUP, singletonList("role"));
        addUserAttribute(userRepresentation, JOB, singletonList("job"));
        addUserAttribute(userRepresentation, CORRECT_ANSWERS, singletonList("0"));
        addUserAttribute(userRepresentation, ROLE, singletonList(ROLE_USER.name()));
        return userRepresentation;
    }

    public List<Answer> mockQuestionAnswer() {
        List<Answer> answerList = new ArrayList<>();
        answerList.add(mockAnswer());
        answerList.add(mockAnswer());
        return answerList;
    }

    private Answer mockAnswer() {
        Answer answer = new Answer();
        answer.setAnswerText("sdas");
        answer.setModelId("id");
        return answer;
    }
}
