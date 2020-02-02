package com.project.qa.model.elasticserach;

import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.elasticserach.Answer;

public class AnswerAsResponse extends Answer {

    private VoteStatus voteStatus;
    private int userScore;

    public VoteStatus getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public AnswerAsResponse(Answer answer, VoteStatus status, int userScore) {
        super(answer.getUserId(), answer.getAnswerText(), answer.isCorrectAnswer(), answer.getPublishDate(), answer.getParentId(), answer.getUserName(), answer.getScore());
        this.voteStatus = status;
        this.modelId = answer.getModelId();
        this.userScore = userScore;
    }

}
