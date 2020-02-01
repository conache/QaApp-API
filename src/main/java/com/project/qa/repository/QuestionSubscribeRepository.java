package com.project.qa.repository;

import com.project.qa.model.QuestionSubscribe;
import com.project.qa.model.QuestionSubscribeIdentity;
import com.project.qa.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionSubscribeRepository  extends JpaRepository<QuestionSubscribe, QuestionSubscribeIdentity> {
}
