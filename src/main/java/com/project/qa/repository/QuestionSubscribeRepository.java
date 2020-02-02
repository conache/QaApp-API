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

    @Query(value = "SELECT user_email from question_subscribe where question_id = :questionId", nativeQuery = true)
    List<String> getUsersEmail(@Param("questionId") String questionId);

    @Query(value = "DELETE from question_subscribe where question_id = :questionId", nativeQuery = true)
    void deleteAllByQuestionId(@Param("questionId") String questionId);

    @Query(value = "SELECT 1 from question_subscribe where question_id = :questionId and user_email =:userEmail", nativeQuery = true)
    boolean isCurrentUserSubscribedToQuestion(@Param("questionId") String questionId, @Param("userEmail") String userEmail);
}
