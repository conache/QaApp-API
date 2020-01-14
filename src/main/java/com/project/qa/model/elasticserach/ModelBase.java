package com.project.qa.model.elasticserach;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.qa.enums.elasticsearch.Index;

public abstract class ModelBase {

    @JsonIgnore
    String id;
    @JsonIgnore
    private final Index index;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType()
    {
        return  this.getClass().getName();
    }

    public ModelBase() {
        this.index = getIndex();
    }


    //Must return the index associated with the model
    public abstract Index getIndex();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public abstract Object getJoinField() throws Exception;
}