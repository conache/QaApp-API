package com.project.qa.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "question_subscribe")
public class QuestionSubscribe {

    @EmbeddedId
    private QuestionSubscribeId questionSubscribeId;

    public QuestionSubscribe() {
    }
    public QuestionSubscribe(QuestionSubscribeId questionSubscribeId){
        this.questionSubscribeId = questionSubscribeId;
    }

    public QuestionSubscribeId getQuestionSubscribeId() {
        return questionSubscribeId;
    }

    public void setQuestionSubscribeId(QuestionSubscribeId questionSubscribeId) {
        this.questionSubscribeId = questionSubscribeId;
    }

}
