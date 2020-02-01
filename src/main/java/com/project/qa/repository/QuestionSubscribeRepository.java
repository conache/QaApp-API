package com.project.qa.repository;

import com.project.qa.model.QuestionSubscribe;
import com.project.qa.model.QuestionSubscribeIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionSubscribeRepository extends JpaRepository<QuestionSubscribe, QuestionSubscribeIdentity> {

    @Query(value = "SELECT userid from question_subscribe where questionid = :questionId", nativeQuery = true)
    String getUserId(String questionId);
}
