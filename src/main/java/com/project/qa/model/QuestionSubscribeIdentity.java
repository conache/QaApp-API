package com.project.qa.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Embeddable
public class QuestionSubscribeIdentity implements Serializable {
    @NotNull
    @Size(max = 255)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String questionId;

    public QuestionSubscribeIdentity() {
    }

    public QuestionSubscribeIdentity(String userId, String questionId) {
        this.userId = userId;
        this.questionId = questionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
