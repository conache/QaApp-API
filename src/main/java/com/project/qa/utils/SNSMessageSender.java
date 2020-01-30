package com.project.qa.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SNSMessageSender {

    private final NotificationMessagingTemplate notificationMessagingTemplate;

    @Autowired
    public SNSMessageSender(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    public void send(String topicName, Object message, String subject) {
        notificationMessagingTemplate.sendNotification(topicName, message, subject);
    }

    public static String test() {
        // Create an Amazon SNS topic.
        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAVB4RJO2SY4I2BN2U", "S9hHOTPa3Iv5IsCzl0bGMrRX1WZ1D1UiuFAcqtu2");
        AmazonSNS amazonSNSClient = AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("us-east-2")
                .build();
        final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
        final CreateTopicResult createTopicResponse = amazonSNSClient.createTopic(createTopicRequest);

// Print the topic ARN.
        System.out.println("TopicArn:" + createTopicResponse.getTopicArn());
    /*    final SNSMessageAttributes message = new SNSMessageAttributes(messageBody);*/
// Print the request ID for the CreateTopicRequest action.
        System.out.println("CreateTopicRequest: " + amazonSNSClient.getCachedResponseMetadata(createTopicRequest));
        return null;
    }
}