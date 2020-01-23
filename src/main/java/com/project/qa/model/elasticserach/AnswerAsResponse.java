package com.project.qa.model.elasticserach;
import com.project.qa.enums.elasticsearch.VoteStatus;
import com.project.qa.model.elasticserach.Answer;

public class AnswerAsResponse extends Answer{

    private VoteStatus voteStatus;

    public VoteStatus getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public AnswerAsResponse(Answer answer, VoteStatus status)
    {
        super(answer.getUserId(),answer.getAnswerText(),answer.isCorrectAnswer(),answer.getPublishDate(), answer.getParentId(), answer.getUserName(), answer.getScore());
        this.voteStatus = status;
        this.modelId = answer.getModelId();
    }

}
