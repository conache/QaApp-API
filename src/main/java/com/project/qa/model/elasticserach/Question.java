package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.qa.enums.elasticsearch.Index;


import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question extends ModelBase {

    private String questionAuthorId;
    private String groupId;
    private int score;
    private String questionTitle;
    private String questionText;
    private Date questionPublishDate;
    @JsonIgnore
    private List<Answer> questionsAnswers;
    private List<String> questionTags;

    public String getQuestionAuthorId() {
        return questionAuthorId;
    }

    public void setQuestionAuthorId(String questionAuthorId) {
        this.questionAuthorId = questionAuthorId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Date getQuestionPublishDate() {
        return questionPublishDate;
    }

    public void setQuestionPublishDate(Date questionPublishDate) {
        this.questionPublishDate = questionPublishDate;
    }

    public List<Answer> getQuestionsAnswers() {
        return questionsAnswers;
    }

    public void setQuestionsAnswers(List<Answer> questionsAnswers) {
        this.questionsAnswers = questionsAnswers;
    }

    public List<String> getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(List<String> questionTags) {
        this.questionTags = questionTags;
    }

    //TODO: Check if automapper is working without the empty constructor
    public Question() {
    }

    public Question(String questionAuthorId, String groupId, int score, String questionTitle, String questionText, Date questionPublishDate, List<Answer> questionsAnswers, List<String> questionTags) {
        this.questionAuthorId = questionAuthorId;
        this.groupId = groupId;
        this.score = score;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.questionPublishDate = questionPublishDate;
        this.questionsAnswers = questionsAnswers;
        this.questionTags = questionTags;
    }

    @Override
    public Index getIndex() {
        return Index.QA;
    }

    @Override
    public String getJoinField() {
        return "question";
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public static class QuestionBuilder {
        private String questionAuthorId;
        private String groupId;
        private int score;
        private String questionTitle;
        private String questionText;
        private Date questionPublishDate;
        private List<Answer> questionsAnswers;
        private List<String> questionTags;

        public QuestionBuilder setQuestionAuthorId(String questionAuthorId) {
            this.questionAuthorId = questionAuthorId;
            return this;
        }

        public QuestionBuilder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public QuestionBuilder setScore(int score) {
            this.score = score;
            return this;
        }

        public QuestionBuilder setQuestionTitle(String questionTitle) {
            this.questionTitle = questionTitle;
            return this;
        }

        public QuestionBuilder setQuestionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        public QuestionBuilder setQuestionPublishDate(Date questionPublishDate) {
            this.questionPublishDate = questionPublishDate;
            return this;
        }

        public QuestionBuilder setQuestionsAnswers(List<Answer> questionsAnswers) {
            this.questionsAnswers = questionsAnswers;
            return this;
        }

        public QuestionBuilder setQuestionTags(List<String> questionTags) {
            this.questionTags = questionTags;
            return this;
        }

        public Question build() {
            return new Question(questionAuthorId, groupId, score, questionTitle, questionText, questionPublishDate, questionsAnswers, questionTags);
        }
    }
}
