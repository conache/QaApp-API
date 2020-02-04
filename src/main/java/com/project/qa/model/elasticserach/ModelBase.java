package com.project.qa.model.elasticserach;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.qa.enums.elasticsearch.Index;
import com.project.qa.utils.Visitor;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelBase {

    String modelId;
    @JsonIgnore
    private final Index index;
    protected int score;
    protected List<String> upVotes;
    protected List<String> downVotes;
    protected String groupName;

    public List<String> getUpVotes() {
        return upVotes;
    }

    public List<String> getDownVotes() {
        return downVotes;
    }

    public void setUpVotes(List<String> upVotes) {
        this.upVotes = upVotes;
    }

    public void setDownVotes(List<String> downVotes) {
        this.downVotes = downVotes;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelType()
    {
        return  this.getClass().getName();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void upVote(String userId) {
        upVotes.add(userId);
        downVotes.remove(userId);
        score = upVotes.size() - downVotes.size();
    }

    public void downVote(String userId) {
        downVotes.add(userId);
        upVotes.remove(userId);
        score = upVotes.size() - downVotes.size();
    }

    public void clearVotes()
    {
        upVotes.clear();
        downVotes.clear();
    }

    public ModelBase() {

        this.index = getIndex();
        score = 0;
        upVotes = new ArrayList<>();
        downVotes = new ArrayList<>();
    }

    //Must return the index associated with the model
    public abstract Index getIndex();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public abstract Object getJoinField() throws Exception;
    public abstract String getSortBy();
    public abstract void accept(Visitor visitor);

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}