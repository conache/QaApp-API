package com.project.qa.service;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.elasticsearch.ModelManager;
import com.project.qa.utils.EncryptUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.qa.enums.Roles.ROLE_USER;
import static com.project.qa.utils.UserUtils.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
/*
    @Mock
    ModelManager<Answer> answerManager;


    @Mock
    ModelManager<Question> questionManager;

    @Mock
    UserService userService;

    @Mock
    PublishNotification publishNotification;

    @Mock
    HttpServletRequest request;

    @Mock
    Pageable pageable;

    @Mock
    private RestHighLevelClient esClient;

    @Before
    public void setUp() {
        esClient = mock(RestHighLevelClient.class);
        answerManager = spy(new ModelManager<>(Answer::new, esClient));
    }

    @Spy
    EncryptUtils encryptUtils;

    @InjectMocks
    AnswerServiceImpl answerService;

   *//* @Test
    public void getAnswersForQuestionShouldReturnPageable() throws IOException {
        UserRepresentation userRepresentation = mockUserRepresentation();
    //    when(elasticClientHandler.createRestClient()).thenReturn(esClient);
        when(userService.findCurrentUser(request)).thenReturn(userRepresentation);
        Pair<List<Answer>, Long> qResult = mockAnswers();
        CountRequest countRequest = new CountRequest();

        when(answerManager.getAnswersForQuestion("id", pageable.getPageSize(), pageable.getPageSize() * (pageable.getPageNumber() - 1), "sort")).thenReturn(qResult);
        when(esClient.count(any(CountRequest.class), any(RequestOptions.class))).thenReturn(any(CountResponse.class));

        when(userService.getUserAnswerScore(request, "userId")).thenReturn(1);

        //when(decrypt("pass","")).thenReturn("caca");
//        doReturn("Expected String").when(encryptUtils).makeStaticWrappedCall();;
        assertNull(answerService.getAnswersForQuestion(request, "id", pageable, "sort"));

    }*//*

    private Pair<List<Answer>, Long> mockAnswers() {
        return Pair.with(new ArrayList<>(), 100L);
    }


    public String makeStaticWrappedCall() {
        return EncryptUtils.decrypt("asa", "asda");
    }

    public CountResponse mockCountResponse() {
        return new CountResponse(10, true, null);
    }

    *//*
      UserRepresentation userRepresentation = userService.findCurrentUser(request);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Pair<List<Answer>, Long> qResult = answerManager.getAnswersForQuestion(questionId, pageSize, pageSize * (pageNumber - 1), sortBy);
        List<AnswerAsResponse> answers = new ArrayList<>();
        for (Answer answer : qResult.getValue0()) {
            VoteStatus status = VoteStatus.NoVote;
            if (answer.getUpVotes().contains(userRepresentation.getId())) {
                status = VoteStatus.UpVote;
            }
            if (answer.getDownVotes().contains(userRepresentation.getId())) {
                status = VoteStatus.DownVote;
            }
            int userScore = userService.getUserAnswerScore(request, answer.getUserId());
            answers.add(new AnswerAsResponse(answer, status, userScore));
        }
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        decrypt(answers, groupName);
        return new Pair<>(answers, qResult.getValue1());
     *//*

    public UserRepresentation mockUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("id");
        userRepresentation.setEmail("testEmail");
        userRepresentation.setFirstName("Dani");
        userRepresentation.setLastName("Printul Banatului");

        userRepresentation.setRequiredActions(defaultRequiredActions);
        userRepresentation.setEnabled(true);

        addUserAttribute(userRepresentation, GROUP, singletonList("role"));
        addUserAttribute(userRepresentation, JOB, singletonList("job"));
        addUserAttribute(userRepresentation, CORRECT_ANSWERS, singletonList("0"));
        addUserAttribute(userRepresentation, ROLE, singletonList(ROLE_USER.name()));
        return userRepresentation;
    }*/
}
