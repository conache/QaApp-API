package com.project.qa.model.elasticserach;

import java.util.Date;

public class ProposedEditQuestion extends Question {
    private String proposedAuthorId;
    private Date proposedDate;
    private String proposedAuthorUsername;


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
