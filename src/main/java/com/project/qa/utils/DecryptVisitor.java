package com.project.qa.utils;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;

public class DecryptVisitor implements Visitor {

    @Override
    public void visit(Question question) {
        String decryptedText = EncryptUtils.decrypt(question.getGroupName(),question.getQuestionText());
        question.setQuestionText(decryptedText);
    }

    @Override
    public void visit(Answer answer) {
        String decryptedText = EncryptUtils.decrypt(answer.getGroupName(),answer.getAnswerText());
        answer.setAnswerText(decryptedText);
    }
}
