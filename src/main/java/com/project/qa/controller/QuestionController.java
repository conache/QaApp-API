package com.project.qa.controller;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.type.ArrayType;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/question")
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
    public Page<Question> findAllGroupQuestions(Pageable pageable) {
        return questionService.findAllGroupQuestions(pageable);
    }

    @GetMapping("/applyFilter")
    public Page<Question> filterQuestions(Pageable page, @RequestParam(required = false) ArrayList<String> tags, @RequestParam(required = false, defaultValue = "questionPublishDate") String sortBy) {
        return questionService.filterAllGroupQuestions(page, tags, sortBy);
    }

    @PostMapping("/add")
    public String addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @ResponseBody
    public String addFoo(@RequestParam(name = "id") String fooId, @RequestParam String name) {
        return "ID: " + fooId + " Name: " + name;
    }
}
