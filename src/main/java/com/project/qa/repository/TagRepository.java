package com.project.qa.repository;

import com.project.qa.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findById(Integer id);

    Page<Tag> findAllByGroupName(String groupName,Pageable page);

    Page<Tag> findAllByGroupNameAndActive(String groupName, boolean active, Pageable pageable);

    Page<Tag> findAllByGroupNameAndQuestionId(String groupName, String questionId,Pageable page);
}
