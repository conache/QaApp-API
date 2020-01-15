package com.project.qa.controller;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(name = "/question")
public class QuestionController {

    public final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/find")
    public Question findById(@RequestParam String questionId) {
        return questionService.findQuestionById(questionId);
    }

    @DeleteMapping("/delete")
    public void deleteById(@RequestParam String questionId) {
        questionService.deleteQuestionById(questionId);
    }

    @GetMapping("/findAll")
    public Page<Question> findAllGroupQuestions(HttpServletRequest request) {
//        return questionService.
        return null;
    }

    @PostMapping("add")
    public String addQuestion(HttpServletRequest request, Question question) {
        return questionService.addQuestion(request, question);
    }
}
