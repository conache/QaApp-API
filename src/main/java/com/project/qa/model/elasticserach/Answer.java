package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.qa.enums.elasticsearch.Index;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.isEmpty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Answer extends ModelBase {

    private String userId;
    private String answerText;
    private int score;
    private boolean isCorrectAnswer;
    private Date publishDate;
    private String questionId;
    private String parentId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public String getParentId() {
        return questionId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String getSortBy() {
        return "publishDate";
    }

    //TODO: Check if automapper is working without the empty constructor
    public Answer() {
    }

    public Answer(String userId, String answerText, int score, boolean isCorrectAnswer, Date publishDate, String questionId) {
        this.userId = userId;
        this.answerText = answerText;
        this.score = score;
        this.isCorrectAnswer = isCorrectAnswer;
        this.publishDate = publishDate;
        this.questionId = questionId;
    }

    @Override
    public Index getIndex() {
        return Index.qa;
    }

    @Override
    public Object getJoinField() {
        if (isEmpty(questionId)) {
            throw new ResponseStatusException(NOT_FOUND, "Question id " + questionId + " not found");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "answer");
        map.put("parent", questionId);
        return map;
    }

    public static class AnswerBuilder {
        private String userId = "";
        private String userName = "";
        private String answerText = "";
        private int score = 0;
        private boolean isCorrectAnswer = false;
        private Date publishDate;
        private String questionId = "";

        public AnswerBuilder setUserId(String userId) {
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}