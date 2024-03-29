package com.project.qa.repository;

import com.project.qa.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findById(Integer id);

    Page<Tag> findAllByGroupName(String groupName, Pageable page);

    Page<Tag> findAllByGroupNameAndActive(String groupName, boolean active, Pageable pageable);

    List<Tag> findAllByGroupNameAndActive(String groupName, boolean active);

    Page<Tag> findAllByGroupNameAndQuestionId(String groupName, String questionId, Pageable page);

    List<Tag> findAllByQuestionId(String questionId);

    @Transactional
    void deleteAllByQuestionId(String modelId);

}
