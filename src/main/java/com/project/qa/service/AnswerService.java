package com.project.qa.service;

import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.Question;
import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnswerService {

    Pair<List<Answer>,Long> getAnswersForQuestion(String questionId, Pageable pageable, String sortBy);
    String addAnswer(Answer answer);
    void addVote(String answerId, String questionId, boolean isUpVote);
    void updateAnswer(Answer answer);
}
