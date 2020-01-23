package com.project.qa.model.elasticserach;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.qa.enums.elasticsearch.Index;

import java.util.ArrayList;

public abstract class ModelBase {

    String modelId;
    @JsonIgnore
    private final Index index;
    protected int score;

    public ArrayList<String> getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(ArrayList<String> upVotes) {
        this.upVotes = upVotes;
    }

    public ArrayList<String> getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(ArrayList<String> downVotes) {
        this.downVotes = downVotes;
    }

    protected ArrayList<String> upVotes;
    protected ArrayList<String> downVotes;


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

    public void upVote(String userId)
    {
        upVotes.add(userId);
        downVotes.remove(userId);
        score = upVotes.size() - downVotes.size();
    }

    public void downVote(String userId)
    {
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


}