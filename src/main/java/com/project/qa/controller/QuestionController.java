package com.project.qa.controller;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public Page<Question> findAllGroupQuestions(HttpServletRequest request){
//        return questionService.
        return null;
    }
}
