package com.project.qa.service;

import com.project.qa.model.Tag;

public interface TagService {
    Integer addTag(Tag tag);

    Tag findTagById(Integer tagId);
}
