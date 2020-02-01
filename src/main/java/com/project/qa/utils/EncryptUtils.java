package com.project.qa.utils;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ModelBase;
import com.project.qa.model.elasticserach.ProposedEditQuestion;
import com.project.qa.model.elasticserach.Question;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.List;

public class EncryptUtils {

    private final static boolean isDebug = true;

    private static StandardPBEStringEncryptor getEncryptor(String key) {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPasswordCharArray(key.toCharArray());
        stringEncryptor.setAlgorithm("PBEWITHMD5ANDDES");
        return stringEncryptor;
    }

    public static String encrypt(String key, String value) {
        return getEncryptor(key).encrypt(value);
    }

    public static String decrypt(String key, String value) {
        return getEncryptor(key).decrypt(value);
    }

    public static void encrypt(ModelBase model, String key)
    {
        if(isDebug)
            return;

        if(model instanceof Question)
        {
            Question question = (Question) model;
            question.setQuestionText(encrypt(key, question.getQuestionText()));
        }

        if(model instanceof Answer)
        {
            Answer answer = (Answer) model;
            answer.setAnswerText(encrypt(key, answer.getAnswerText()));
        }

        if(model instanceof ProposedEditQuestion)
        {
            ProposedEditQuestion proposedEditQuestion = (ProposedEditQuestion) model;
            proposedEditQuestion.setQuestionText(encrypt(key, proposedEditQuestion.getQuestionText()));
        }
    }

    public static void decrypt(ModelBase model, String key)
    {
        if(isDebug)
            return;
        
        if(model instanceof Question)
        {
            Question question = (Question) model;
            question.setQuestionText(decrypt(key, question.getQuestionText()));
        }

        if(model instanceof Answer)
        {
            Answer answer = (Answer) model;
            answer.setAnswerText(decrypt(key, answer.getAnswerText()));
        }

        if(model instanceof ProposedEditQuestion)
        {
            ProposedEditQuestion proposedEditQuestion = (ProposedEditQuestion) model;
            proposedEditQuestion.setQuestionText(decrypt(key, proposedEditQuestion.getQuestionText()));
        }
    }

    public static void encrypt(List<?extends ModelBase> models, String key)
    {
        for (ModelBase model: models) {
            encrypt(model,key);
        }
    }

    public static void decrypt(List<?extends ModelBase> models, String key)
    {
        for (ModelBase model: models) {
            decrypt(model,key);
        }
    }
}
