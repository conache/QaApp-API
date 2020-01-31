package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposedEditQuestion extends Question {
    private String proposedAuthorId;
    private Date proposedDate;
    private String proposedAuthorUsername;
    private String parentQuestionId;

    public ProposedEditQuestion() {
    }

    public ProposedEditQuestion(String proposedAuthorId, Date proposedDate, String proposedAuthorUsername, String parentQuestionId) {
        this.proposedAuthorId = proposedAuthorId;
        this.proposedDate = proposedDate;
        this.proposedAuthorUsername = proposedAuthorUsername;
        this.parentQuestionId = parentQuestionId;
    }


    public ProposedEditQuestion(Question question) {
        super(question.getQuestionAuthorId(), question.getQuestionAuthorName(), question.getGroupName(), question.getQuestionTitle(), question.getQuestionText(), question.getQuestionPublishDate(), question.getNoAnswers(), question.getQuestionsAnswers(), question.getQuestionTags());
    }

    public String getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(String parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public String getProposedAuthorId() {
        return proposedAuthorId;
    }

    public void setProposedAuthorId(String proposedAuthorId) {
        this.proposedAuthorId = proposedAuthorId;
    }

    public Date getProposedDate() {
        return proposedDate;
    }

    public void setProposedDate(Date proposedDate) {
        this.proposedDate = proposedDate;
    }

    public void setProposedAuthorUsername(String proposedAuthorUsername) {
        this.proposedAuthorUsername = proposedAuthorUsername;
    }

    public String getProposedAuthorUsername() {
        return proposedAuthorUsername;
    }
}
