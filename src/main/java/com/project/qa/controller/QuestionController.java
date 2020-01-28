package com.project.qa.controller;

import com.project.qa.model.elasticserach.Question;
import com.project.qa.model.elasticserach.QuestionAsResponse;
import com.project.qa.service.QuestionService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public QuestionAsResponse findById(HttpServletRequest request, @RequestParam String questionId) {
        return questionService.findQuestionById(request, questionId);
    }

    @DeleteMapping("/delete")
    public void deleteById(@RequestParam String questionId) {
        questionService.deleteQuestionById(questionId);
    }

    @GetMapping("/findAll")
    public Pair<List<Question>, Long> filterQuestions(HttpServletRequest request, Pageable page, @RequestParam(required = false) ArrayList<String> tags, @RequestParam(required = false, defaultValue = "questionPublishDate") String sortBy) {
        return questionService.filterAllGroupQuestions(request, page, tags, sortBy);
    }

    @PostMapping("/add")
    public String addQuestion(HttpServletRequest request, @RequestBody Map<String, Object> questionRequest) {
        return questionService.addQuestion(request, questionRequest);
    }

    @GetMapping("/search")
    public List<Question> searchQuestions(HttpServletRequest request, @RequestParam String text, @RequestParam(required = false, defaultValue = "5") int maxSize) {
        return questionService.search(request, text, maxSize);
    }

    @PutMapping("/vote")
    public void voteQuestion(HttpServletRequest request, @RequestParam String questionId, @RequestParam boolean isUpVote) {
        questionService.voteQuestion(request, questionId, isUpVote);
    }

    @PostMapping("/update")
    public void updateQuestion(@RequestBody Question question) {
        questionService.updateQuestion(question);
    }
}
