package com.project.qa.service;

import com.project.qa.model.elasticserach.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface QuestionService {
    Question findQuestionById(String questionId);

    void deleteQuestionById(String questionId);

    Page<Question> findAllGroupQuestions(HttpServletRequest request, Pageable pageable);

    String addQuestion(HttpServletRequest request, Question question);
}
