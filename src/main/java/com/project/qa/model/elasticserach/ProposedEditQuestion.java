package com.project.qa.model.elasticserach;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposedEditQuestion extends Question {
    private String proposedAuthorId;
    private Date proposedDate;
    private String proposedAuthorUsername;

    public ProposedEditQuestion() {
    }

    public ProposedEditQuestion(String proposedAuthorId, Date proposedDate, String proposedAuthorUsername) {
        this.proposedAuthorId = proposedAuthorId;
        this.proposedDate = proposedDate;
        this.proposedAuthorUsername = proposedAuthorUsername;
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
