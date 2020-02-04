package com.project.qa.utils;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;

public interface Visitor {

     void visitQuestion(Question question);
     void visitAnswer(Answer answer);
}
