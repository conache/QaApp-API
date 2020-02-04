package com.project.qa.model.elasticserach;

import com.project.qa.enums.elasticsearch.VoteStatus;

public class QuestionAsResponse extends Question {

    private VoteStatus voteStatus;
    private int userScore;
    private boolean isCurrentUserSubscribed;

    public VoteStatus getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public int getUserScore() {
        return userScore;}

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public boolean isCurrentUserSubscribed() {
        return isCurrentUserSubscribed; }

    public void setCurrentUserSubscribed(boolean currentUserSubscribed) {
        isCurrentUserSubscribed = currentUserSubscribed;
    }

     private QuestionAsResponse(Question question, VoteStatus status, int userScore, boolean isCurrentUserSubscribed) {
        super(question.getQuestionAuthorId(),question.getQuestionAuthorName(), question.getGroupName(), question.getQuestionTitle(), question.getQuestionText(), question.getQuestionPublishDate(),question.getNoAnswers(), question.getQuestionsAnswers(),question.getQuestionTags());
        this.voteStatus = status;
        this.isCurrentUserSubscribed = isCurrentUserSubscribed;
        this.score = question.getScore();
        this.userScore = userScore;
        this.modelId = question.getModelId();
    }

    static public class QuestionAsResponseBuilder {
        private Question question;
        private VoteStatus status;
        private int userScore;
        private boolean isCurrentUserSubscribed;

        public QuestionAsResponseBuilder setQuestion(Question question) {
            this.question = question;
            return this;
        }

        public QuestionAsResponseBuilder setStatus(VoteStatus status) {
            this.status = status;
            return this;
        }

        public QuestionAsResponseBuilder setUserScore(int userScore) {
            this.userScore = userScore;
            return this;
        }

        public QuestionAsResponseBuilder setIsCurrentUserSubscribed(boolean isCurrentUserSubscribed) {
            this.isCurrentUserSubscribed = isCurrentUserSubscribed;
            return this;
        }

        public QuestionAsResponse build() {
            return new QuestionAsResponse(question, status, userScore, isCurrentUserSubscribed);
        }
    }
}
