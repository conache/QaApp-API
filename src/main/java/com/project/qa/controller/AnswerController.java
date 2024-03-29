package com.project.qa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.AnswerAsResponse;
import com.project.qa.service.AnswerService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.javatuples.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(path = "/answer")
public class AnswerController {

    public final AnswerService answerService;

    public AnswerController(AnswerService questionService) {
        this.answerService = questionService;
    }


    @GetMapping("/getAnswers")
    public Pair<List<AnswerAsResponse>, Long> getAnswersForQuestion(HttpServletRequest request, Pageable page, @RequestParam String questionId, @RequestParam(required = false, defaultValue = "publishDate") String sortBy) {
        return answerService.getAnswersForQuestion(request, questionId, page, sortBy);
    }

    @PostMapping("/addAnswer")
    public String getAnswersForQuestion(HttpServletRequest request, @RequestBody Answer answer) throws JsonProcessingException {
        return answerService.addAnswer(request, answer);
    }

    @PutMapping("/vote")
    public void addVote(HttpServletRequest request, @RequestParam String answerId, @RequestParam String questionId, @RequestParam boolean isUpVote) {
        answerService.addVote(request, answerId, questionId, isUpVote);
    }

    @PostMapping("/update")
    public void updateAnswer(HttpServletRequest request, @RequestBody Answer answer) {
        answerService.updateAnswer(request, answer);
    }

    @DeleteMapping("/delete")
    public void deleteAnswer(@RequestParam String answerId, @RequestParam String questionId) {
        answerService.deleteAnswer(answerId, questionId);
    }

    @PutMapping("/markCorrect")
    public void markCorrectAnswer(HttpServletRequest request, @RequestParam String answerId, @RequestParam String questionId) throws JsonProcessingException {
        answerService.markCorrectAnswer(request, answerId, questionId);
    }
}
