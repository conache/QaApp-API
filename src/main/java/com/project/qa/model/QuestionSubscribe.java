package com.project.qa.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "question_subscribe")
public class QuestionSubscribe {

    @EmbeddedId
    private QuestionSubscribeIdentity questionSubscribeIdentity;

    public QuestionSubscribe() {
    }
    public QuestionSubscribe(QuestionSubscribeIdentity questionSubscribeIdentity){
        this.questionSubscribeIdentity= questionSubscribeIdentity;
    }

    public QuestionSubscribeIdentity getQuestionSubscribeIdentity() {
        return questionSubscribeIdentity;
    }

    public void setQuestionSubscribeIdentity(QuestionSubscribeIdentity questionSubscribeIdentity) {
        this.questionSubscribeIdentity = questionSubscribeIdentity;
    }

}
