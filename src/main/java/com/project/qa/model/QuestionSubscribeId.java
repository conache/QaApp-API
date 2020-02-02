package com.project.qa.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Embeddable
public class QuestionSubscribeId implements Serializable {
    @NotNull
    @Column(name = "user_email")
    @Size(max = 255)
    private String userEmail;

    @NotNull
    @Column(name = "question_id")
    @Size(max = 255)
    private String questionId;

    public QuestionSubscribeId() {
    }

    public QuestionSubscribeId(String userEmail, String questionId) {
        this.userEmail = userEmail;
        this.questionId = questionId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userId) {
        this.userEmail = userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionSubscribeId that = (QuestionSubscribeId) o;
        return Objects.equals(userEmail, that.userEmail) &&
                Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, questionId);
    }
}
