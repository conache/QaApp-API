package com.project.qa.service;

import com.project.qa.model.Tag;
import com.project.qa.repository.TagRepository;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TagServiceImpl implements TagService {


    final TagRepository repository;

    @Autowired
    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer addTag(Tag tag) {
        Tag savedTag = repository.save(tag);
        return savedTag.getId();
    }

    @Override
    public Tag findTagById(Integer tagId) {
        Optional<Tag> byId = repository.findById(tagId);
        return byId.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User doesn't have attributes"));
    }
}
