package com.project.qa.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qa.config.aws.AwsCredentials;
import com.project.qa.enums.NotificationTypeEnum;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.Notification;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.AnswerAsResponse;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.QuestionSubscribeRepository;
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
import java.util.HashMap;
import java.util.List;

import static com.project.qa.utils.EncryptUtils.decrypt;
import static com.project.qa.utils.EncryptUtils.encrypt;
import static com.project.qa.utils.UserUtils.GROUP;
import static com.project.qa.utils.UserUtils.getUserAttribute;


@Service
public class AnswerServiceImpl implements AnswerService {

    private final ModelManager<Answer> answerManager;
    private final ModelManager<Question> questionManager;
    private final UserService userService;
    private final AwsCredentials credentials;
    private final QuestionSubscribeRepository questionSubscribeRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public AnswerServiceImpl(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, UserService userService, AwsCredentials credentials, QuestionSubscribeRepository questionSubscribeRepository, ObjectMapper objectMapper) {
        this.answerManager = new ModelManager<>(Answer::new, esClient);
        this.questionManager = new ModelManager<>(Question::new, esClient);
        this.userService = userService;
        this.credentials = credentials;
        this.questionSubscribeRepository = questionSubscribeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Pair<List<AnswerAsResponse>, Long> getAnswersForQuestion(HttpServletRequest request, String questionId, Pageable pageable, String sortBy) {

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
            answers.add(new AnswerAsResponse(answer, status));
        }
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        decrypt(answers, groupName);
        return new Pair<>(answers, qResult.getValue1());

    }

    @Override
    public String addAnswer(HttpServletRequest request, Answer answer) throws JsonProcessingException {

        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        answer.setUserId(userRepresentation.getId());
        answer.setUserName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
        answer.setPublishDate(new Date());
        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        encrypt(answer, groupName);

        String answerId = answerManager.index(answer);
        if (answerId != null) {
            Question q = questionManager.getByID(answer.getParentId());
            q.setNoAnswers(q.getNoAnswers() + 1);
            questionManager.update(q);
            publishNotification(q);
        }

        return answerId;
    }

    private void publishNotification(Question question) throws JsonProcessingException {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(credentials.getAccessKey(), credentials.getSecretKey());
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(credentialsProvider).withRegion(credentials.getRegion()).build();
        HashMap<String, Object> result = new HashMap<>();
        result.put("users", questionSubscribeRepository.getUsersEmail(question.getModelId()));
        Notification notification = new Notification();
        notification.setObjectId(question.getModelId());
        notification.setNotificationType(NotificationTypeEnum.QUESTION);
        notification.setNotificationText("New answer for: " + question.getQuestionTitle());
        result.put("notification", objectMapper.writeValueAsString(notification));


        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(credentials.getAwsSQSURL())
                .withMessageBody(result.toString())
                .withMessageGroupId("1")
                .withMessageDeduplicationId("1");

        sqs.sendMessage(send_msg_request);
    }

    @Override
    public void addVote(HttpServletRequest request, String answerId, String questionId, boolean isUpVote) {

        Answer answer = answerManager.getByID(answerId, questionId);
        UserRepresentation userRepresentation = userService.findCurrentUser(request);
        if (isUpVote) {
            answer.upVote(userRepresentation.getId());
        } else {
            answer.downVote(userRepresentation.getId());
        }
        answerManager.update(answer, questionId);
    }

    @Override
    public void updateAnswer(HttpServletRequest request, Answer answer) {
        Answer originalAnswer = answerManager.getByID(answer.getModelId(), answer.getParentId());
        answer.setDownVotes(originalAnswer.getDownVotes());
        answer.setUpVotes(originalAnswer.getUpVotes());
        answer.setScore(originalAnswer.getScore());
        answer.setUserName(originalAnswer.getUserName());
        answer.setUserId(originalAnswer.getUserId());
        answer.setPublishDate(DateTime.now().toDate());
        answer.setCorrectAnswer(originalAnswer.isCorrectAnswer());

        UserRepresentation user = userService.findCurrentUser(request);
        List<String> userGroups = getUserAttribute(user, GROUP);
        String groupName = userGroups.get(0);

        encrypt(answer, groupName);
        answerManager.update(answer, answer.getParentId());

    }

    @Override
    public void deleteAnswer(String answerId, String questionId) {
        answerManager.delete(answerId, questionId);

        Question question = questionManager.getByID(questionId);
        int noAnswers = question.getNoAnswers();
        question.setNoAnswers(noAnswers - 1);
        questionManager.update(question);

    }

    @Override
    public void markCorrectAnswer(String answerId, String questionId) {
        Answer correctAnswer = answerManager.getByID(answerId, questionId);
        Question question = questionManager.getByID(correctAnswer.getParentId());

        answerManager.loadAnswers(question);

        List<Answer> questionsAnswers = question.getQuestionsAnswers();
        for (Answer answer : questionsAnswers) {
            if (answer.isCorrectAnswer()) {
                answer.setCorrectAnswer(false);
                answerManager.update(answer, question.getModelId());
            }
        }
        correctAnswer.setCorrectAnswer(true);
        answerManager.update(correctAnswer, correctAnswer.getParentId());
    }
}
