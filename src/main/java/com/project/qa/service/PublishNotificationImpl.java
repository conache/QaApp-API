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
import com.project.qa.model.Notification;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.repository.QuestionSubscribeRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class PublishNotificationImpl implements PublishNotification {

    private final AwsCredentials credentials;
    private final QuestionSubscribeRepository questionSubscribeRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PublishNotificationImpl(AwsCredentials credentials, QuestionSubscribeRepository questionSubscribeRepository, ObjectMapper objectMapper) {
        this.credentials = credentials;
        this.questionSubscribeRepository = questionSubscribeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void pushNotificationOnNewAnswer(Question question, UserRepresentation userRepresentation) throws JsonProcessingException {
        List<String> usersEmail = questionSubscribeRepository.getUsersEmail(question.getModelId());
        usersEmail.remove(userRepresentation.getEmail());
        Notification notification = new Notification();
        notification.setObjectId(question.getModelId());
        notification.setNotificationType(NotificationTypeEnum.QUESTION);
        notification.setNotificationText("New answer for: " + question.getQuestionTitle());
        publishNotification(usersEmail, notification);
    }

    @Override
    public void pushNotificationOnProposedQuestion(Question question, UserRepresentation userRepresentation) throws JsonProcessingException {
        List<String> userEmail = singletonList(userRepresentation.getEmail());
        Notification notification = new Notification();
        notification.setObjectId(question.getModelId());
        notification.setNotificationType(NotificationTypeEnum.PROPOSED_QUESTION);
        notification.setNotificationText("New proposed edit question for: " + question.getQuestionTitle());
        publishNotification(userEmail, notification);
    }

    @Override
    public void pushNotificationOnCorrectAnswer(UserRepresentation userRepresentation, Answer correctAnswer) throws JsonProcessingException {
        List<String> userEmail = singletonList(userRepresentation.getEmail());
        Notification notification = new Notification();
        notification.setObjectId(correctAnswer.getParentId());
        notification.setNotificationType(NotificationTypeEnum.CORRECT_ANSWER);
        notification.setNotificationText("Your answer was marked as correct!");
        publishNotification(userEmail, notification);
    }

    private void publishNotification(List<String> usersEmail, Notification notification) throws JsonProcessingException {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(credentials.getAccessKey(), credentials.getSecretKey());
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(credentialsProvider).withRegion(credentials.getRegion()).build();
        HashMap<String, Object> result = new HashMap<>();
        result.put("users", usersEmail);
        result.put("notification", objectMapper.writeValueAsString(notification));


        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(credentials.getAwsSQSURL())
                .withMessageBody(objectMapper.writeValueAsString(result));

        sqs.sendMessage(send_msg_request);
    }
}