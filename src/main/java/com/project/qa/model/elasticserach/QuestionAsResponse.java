package com.project.qa.model.elasticserach;

import com.project.qa.enums.elasticsearch.VoteStatus;

public class QuestionAsResponse extends Question {

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

    public QuestionAsResponse(Question question, VoteStatus status, int userScore) {
        super(question.getQuestionAuthorId(), question.getGroupName(), question.getQuestionTitle(), question.getQuestionText(), question.getQuestionPublishDate(), null, question.getQuestionTags());
        this.voteStatus = status;
        this.modelId = question.getModelId();
        this.score = question.getScore();
        this.setQuestionAuthorName(question.getQuestionAuthorName());
        this.userScore = userScore;
    }
}
