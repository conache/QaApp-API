package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.qa.enums.elasticsearch.Index;


import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question extends ModelBase {

    private String questionAuthorId;
    private String questionAuthorName;
    private String groupName;
    private String questionTitle;
    private String questionText;
    private Date questionPublishDate;
    private int noAnswers;
    @JsonIgnore
    private List<Answer> questionsAnswers;
    private List<String> questionTags;

    public String getQuestionAuthorId() {
        return questionAuthorId;
    }

    public void setQuestionAuthorId(String questionAuthorId) {
        this.questionAuthorId = questionAuthorId;
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

    public Question(String questionAuthorId, String groupName, String questionTitle, String questionText, Date questionPublishDate, List<Answer> questionsAnswers, List<String> questionTags) {
        this.questionAuthorId = questionAuthorId;
        this.groupName = groupName;

        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.questionPublishDate = questionPublishDate;
        this.questionsAnswers = questionsAnswers;
        this.questionTags = questionTags;
    }

    @Override
    public Index getIndex() {
        return Index.qa;
    }

    @Override
    public String getJoinField() {
        return "question";
    }

    @Override
    public String getSortBy() {
        return "questionPublishDate";
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNoAnswers() {
        return noAnswers;
    }

    public void setNoAnswers(int noAnswers) {
        this.noAnswers = noAnswers;
    }

    public String getQuestionAuthorName() {
        return questionAuthorName;
    }

    public void setQuestionAuthorName(String questionAuthorName) {
        this.questionAuthorName = questionAuthorName;
    }


    public static class QuestionBuilder {
        private String questionAuthorId;
        private String groupId;
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
            return new Question(questionAuthorId, groupId, questionTitle, questionText, questionPublishDate, questionsAnswers, questionTags);
        }
    }
}
