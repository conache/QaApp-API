package com.project.qa.service;

import com.project.qa.model.elasticserach.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface QuestionService {
    Question findQuestionById(String questionId);

    void deleteQuestionById(String questionId);

    Page<Question> findAllGroupQuestions(Pageable pageable);

    Page<Question> filterAllGroupQuestions(Pageable pageable, List<String> tags, String sortBy);

    String addQuestion(Question question);
}
