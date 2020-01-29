package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposedEditQuestion extends Question {
    private String proposedAuthorId;
    private Date proposedDate;
    private String proposedAuthorUsername;
    private String parentQuestionId;

    public ProposedEditQuestion() {
        super();
    }

    public ProposedEditQuestion(String proposedAuthorId, Date proposedDate, String proposedAuthorUsername, String parentQuestionId) {
        this.proposedAuthorId = proposedAuthorId;
        this.proposedDate = proposedDate;
        this.proposedAuthorUsername = proposedAuthorUsername;
        this.parentQuestionId = parentQuestionId;
    }

    public ProposedEditQuestion(String questionAuthorId, String groupName, String questionTitle, String questionText, Date questionPublishDate, List<Answer> questionsAnswers, List<String> questionTags) {
        super(questionAuthorId, groupName, questionTitle, questionText, questionPublishDate, questionsAnswers, questionTags);
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
