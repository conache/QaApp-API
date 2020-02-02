package com.project.qa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.qa.model.elasticserach.ProposedEditQuestion;
import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface QuestionService {

    QuestionAsResponse findQuestionById(HttpServletRequest request, String questionId);


    void deleteQuestionById(String questionId);

    Pair<List<QuestionAsResponse>, Long> findAllGroupQuestions(HttpServletRequest request, Pageable pageable);

    Pair<List<QuestionAsResponse>, Long> findCurrentUserQuestions(HttpServletRequest request, Pageable pageable, String sortBy);

    Pair<List<QuestionAsResponse>, Long> filterAllGroupQuestions(HttpServletRequest request, Pageable pageable, List<String> tags, String sortBy);

    List<Question> search(HttpServletRequest request, String text, int maxSize);

    String addQuestion(HttpServletRequest request, Map<String, Object> questionRequest);

    void voteQuestion(HttpServletRequest request, String questionId, boolean isUpVote);

    void editQuestion(HttpServletRequest request, Map<String, Object> objectMap);

    void editQuestion(HttpServletRequest request, Question question, List<String> tags);

    void appendTagToQuestion(Integer tagId);

    Pair<List<ProposedEditQuestion>, Long> findAllUserProposedQuestions(HttpServletRequest request, Pageable pageable, String sortBy);

    String addProposedQuestion(HttpServletRequest request, Map<String, Object> questionValues) throws JsonProcessingException;

    void deleteProposedEditQuestionById(HttpServletRequest request, String proposedQuestionId);

    void acceptProposedQuestion(HttpServletRequest request, String proposedQuestionId);

    Map<String, Object> findProposedEditQuestion(HttpServletRequest request, String proposedQuestionId);

    void subscribeToQuestion(HttpServletRequest request, String questionId);

    void unsubscribeFromQuestion(HttpServletRequest request, String questionId);
}
