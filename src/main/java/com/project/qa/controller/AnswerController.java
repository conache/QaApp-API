package com.project.qa.controller;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.AnswerAsResponse;
import com.project.qa.service.AnswerService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.javatuples.Pair;

import java.util.List;


@RestController
@RequestMapping(path = "/answer")
public class AnswerController {

    public final AnswerService answerService;

    public AnswerController(AnswerService questionService) {
        this.answerService = questionService;
    }


    @GetMapping("/getAnswers")
    public Pair<List<AnswerAsResponse>, Long> getAnswersForQuestion(Pageable page, @RequestParam String questionId, @RequestParam(required = false, defaultValue = "publishDate") String sortBy)
    {
        return answerService.getAnswersForQuestion(questionId, page, sortBy);
    }

    @PostMapping("/addAnswer")
    public String getAnswersForQuestion(@RequestBody Answer answer)
    {
        return answerService.addAnswer(answer);
    }

    @PutMapping("/vote")
    public void addVote(@RequestParam String answerId, @RequestParam String questionId, @RequestParam boolean isUpVote)
    {
        answerService.addVote(answerId,questionId,isUpVote);
    }

    @PostMapping("/update")
    public void updateAnswer(@RequestBody Answer answer)
    {
        answerService.updateAnswer(answer);
    }

    @DeleteMapping("/update")
    public void deleteAnswer(@RequestParam String answerId, @RequestParam String questionId)
    {
        answerService.deleteAnswer(answerId, questionId);
    }



}
