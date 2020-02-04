package com.project.qa.utils;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;

public class EncryptVisitor implements Visitor {
    @Override
    public void visit(Question question) {
        String encryptedText = EncryptUtils.encrypt(question.getGroupName(),question.getQuestionText());
        question.setQuestionText(encryptedText);
    }

    @Override
    public void visit(Answer answer) {
        String encryptedText = EncryptUtils.encrypt(answer.getGroupName(),answer.getAnswerText());
        answer.setAnswerText(encryptedText);
    }
}
