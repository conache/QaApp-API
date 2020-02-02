package com.project.qa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.qa.model.elasticserach.Question;
import org.keycloak.representations.idm.UserRepresentation;

public interface PublishNotification {
    void pushNotificationOnNewAnswer(Question question, UserRepresentation userRepresentation) throws JsonProcessingException;

    void pushNotificationOnProposedQuestion(Question question, UserRepresentation userRepresentation) throws JsonProcessingException;
}
