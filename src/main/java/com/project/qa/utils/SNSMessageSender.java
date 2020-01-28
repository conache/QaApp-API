package com.project.qa.utils;

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
}