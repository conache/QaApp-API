package com.project.qa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.AnswerAsResponse;
import com.project.qa.model.elasticserach.Question;
import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AnswerService {

    Pair<List<AnswerAsResponse>, Long> getAnswersForQuestion(HttpServletRequest request, String questionId, Pageable pageable, String sortBy);

    String addAnswer(HttpServletRequest request, Answer answer) throws JsonProcessingException;

    void addVote(HttpServletRequest request, String answerId, String questionId, boolean isUpVote);

    void updateAnswer(HttpServletRequest request, Answer answer);

    void deleteAnswer(String answerId, String questionId);

    void markCorrectAnswer(HttpServletRequest request, String answerId, String questionId) throws JsonProcessingException;

}
