package com.project.qa.service;

import com.project.qa.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Integer addTag(Tag tag);

    Tag findTagById(Integer tagId);

    Page<Tag> findAllByGroupIdAndActive(String groupId, boolean active, Pageable pageable);

    List<Tag> findAllByGroupIdAndActive(String groupName, boolean active);

    void deleteTagById(Integer tagId);

    void acceptTag(Integer tagId);

    void deleteTagsByQuestionId(String modelId);

}
