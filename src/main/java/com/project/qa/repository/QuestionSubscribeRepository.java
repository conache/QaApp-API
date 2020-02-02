package com.project.qa.repository;

import com.project.qa.model.QuestionSubscribe;
import com.project.qa.model.QuestionSubscribeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionSubscribeRepository extends JpaRepository<QuestionSubscribe, QuestionSubscribeId> {

    @Query(value = "SELECT user_id from question_subscribe where question_id = :questionId", nativeQuery = true)
    List<String> getUserIds(@Param("questionId") String questionId);
}
