package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.qa.enums.elasticsearch.Index;
import java.util.Date;
import java.util.HashMap;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Answer extends ModelBase {

    private int userId;
    private String answerText;
    private int score;
    private boolean isCorrectAnswer;
    private Date publishDate;
    private String questionId;
    private String parentId;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }
    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }
    public Date getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
    public String getParentId(){ return questionId;}
    public void setParentId(String parentId) {this.parentId = parentId;}
    public String getQuestionId() { return questionId;}
    public void setQuestionId(String questionId) {this.questionId = questionId; }

    //TODO: Check if automapper is working without the empty constructor
    public Answer(){}

    public Answer(int userId, String answerText, int score, boolean isCorrectAnswer, Date publishDate, String questionId) {

        this.userId = userId;
        this.answerText = answerText;
        this.score = score;
        this.isCorrectAnswer = isCorrectAnswer;
        this.publishDate = publishDate;
        this.questionId = questionId;
    }

    @Override
    public Index getIndex() { return Index.QA; }

    @Override
    public Object getJoinField() throws Exception {
        if(questionId == null || questionId == "")
        {
            throw new Exception();
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "answer");
        map.put("parent", questionId);
        return  map;
    }

    public static class AnswerBuilder {
        private int userId = 0;
        private String answerText = "";
        private int score = 0;
        private boolean isCorrectAnswer = false;
        private Date publishDate;
        private String questionId = "";

        public AnswerBuilder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public AnswerBuilder setAnswerText(String answerText) {
            this.answerText = answerText;
            return this;
        }

        public AnswerBuilder setScore(int score) {
            this.score = score;
            return this;
        }

        public AnswerBuilder setIsCorrectAnswer(boolean isCorrectAnswer) {
            this.isCorrectAnswer = isCorrectAnswer;
            return this;
        }

        public AnswerBuilder setPublishDate(Date publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public AnswerBuilder setQuestionId(String questionId) {
            this.questionId = questionId;
            return this;
        }

        public Answer build() {
            return new Answer(userId, answerText, score, isCorrectAnswer, publishDate, questionId);
        }
    }

}