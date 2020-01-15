package com.project.qa.service;

import com.project.qa.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Integer addTag(Tag tag);

    Tag findTagById(Integer tagId);

    Page<Tag> findAllByGroupName(String groupId,Pageable page);

    Page<Tag> findAllByGroupIdAndActive(String groupId, boolean active, Pageable pageable);

    Page<Tag> findAllByGroupIdAndQuestionId(String groupId, String questionId,Pageable pageable);

    void deleteTagById(Integer tagId);
}
