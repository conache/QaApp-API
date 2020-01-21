package com.project.qa.model.elasticserach;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.qa.enums.elasticsearch.Index;

public abstract class ModelBase {

    String modelId;
    @JsonIgnore
    private final Index index;

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

    public ModelBase() {
        this.index = getIndex();
    }


    //Must return the index associated with the model
    public abstract Index getIndex();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public abstract Object getJoinField() throws Exception;
    public abstract String getSortBy();

}