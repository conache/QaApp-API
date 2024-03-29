package com.project.qa.service;

import com.project.qa.model.Tag;
import com.project.qa.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    @Override
    public Page<Tag> findAllByGroupIdAndActive(String groupName, boolean active, Pageable pageable) {
        return repository.findAllByGroupNameAndActive(groupName, active, pageable);
    }

    @Override
    public List<Tag> findAllByGroupIdAndActive(String groupName, boolean active) {
        return repository.findAllByGroupNameAndActive(groupName, active);
    }

    @Override
    public void deleteTagById(Integer tagId) {
        repository.deleteById(tagId);
    }

    @Override
    public void acceptTag(Integer tagId) {
        Optional<Tag> tagOptional = repository.findById(tagId);
        if (tagOptional.isPresent()) {
            Tag tag = tagOptional.get();
            tag.setActive(true);
            repository.save(tag);
        }
    }

    @Override
    public void deleteTagsByQuestionId(String modelId) {
        repository.deleteAllByQuestionId(modelId);
    }

}
