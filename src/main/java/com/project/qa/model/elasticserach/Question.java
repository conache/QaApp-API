package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.qa.enums.elasticsearch.Index;
import com.project.qa.utils.Visitor;


import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question extends ModelBase {

    private String questionAuthorId;
    private String questionAuthorName;
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
        return questionTags;}

    public void setQuestionTags(List<String> questionTags) {
        this.questionTags = questionTags; }

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

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    public Question() {} //needed for automapper

//    public Question(String questionAuthorId, String groupName, String questionTitle, String questionText, Date questionPublishDate, List<Answer> questionsAnswers, List<String> questionTags) {
//        this.questionAuthorId = questionAuthorId;
//        this.groupName = groupName;
//        this.questionTitle = questionTitle;
//        this.questionText = questionText;
//        this.questionPublishDate = questionPublishDate;
//        this.questionsAnswers = questionsAnswers;
//        this.questionTags = questionTags;
//    }

    public Question(String questionAuthorId, String questionAuthorName, String groupName, String questionTitle, String questionText, Date questionPublishDate, int noAnswers, List<Answer> questionsAnswers, List<String> questionTags) {
        this.questionAuthorId = questionAuthorId;
        this.questionAuthorName = questionAuthorName;
        this.groupName = groupName;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.questionPublishDate = questionPublishDate;
        this.noAnswers = noAnswers;
        this.questionsAnswers = questionsAnswers;
        this.questionTags = questionTags;
    }

}
