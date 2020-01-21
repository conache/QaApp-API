package com.project.qa.controller;
import com.project.qa.model.elasticserach.Answer;
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
    public Pair<List<Answer>, Long> getAnswersForQuestion(Pageable page, @RequestParam String questionId, @RequestParam(required = false, defaultValue = "publishDate") String sortBy)
    {
        return answerService.getAnswersForQuestion(questionId, page, sortBy);
    }

    @PostMapping("/addAnswer")
    public String getAnswersForQuestion(@RequestBody Answer answer)
    {
        return answerService.addAnswer(answer);
    }

}
