package com.project.qa.model.elasticserach;

import com.project.qa.enums.elasticsearch.VoteStatus;

public class QuestionAsResponse extends Question {

    private VoteStatus voteStatus;

    public VoteStatus getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public QuestionAsResponse(Question question, VoteStatus status)
    {
        super(question.getQuestionAuthorId(),question.getGroupName(),question.getQuestionTitle(),question.getQuestionText(),question.getQuestionPublishDate(),null,question.getQuestionTags());
        this.voteStatus = status;
        this.modelId = question.getModelId();
        this.score = question.getScore();
        this.setQuestionAuthorName(question.getQuestionAuthorName());
    }
}