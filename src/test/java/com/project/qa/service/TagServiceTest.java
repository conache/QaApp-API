package com.project.qa.service;


import com.project.qa.model.Tag;
import com.project.qa.repository.TagRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @Mock
    Pageable pageable;

    @Spy
    @InjectMocks
    TagServiceImpl tagService;

    @Test
    public void testAddTag() {
        Tag tag = mockTag();
        when(tagRepository.save(tag)).thenReturn(tag);
        Integer resultedTagId = tagService.addTag(tag);
        assertEquals(tag.getId(), resultedTagId);
    }

    @Test
    public void testFindTagById() {
        Integer tagId = 1;
        Optional<Tag> optionalTag = mockTagOptional();
        when(tagRepository.findById(tagId)).thenReturn(optionalTag);
        Tag resultedTag = tagService.findTagById(tagId);
        assertEquals(tagId, resultedTag.getId());
    }

    @Test
    public void testFindAllByGroupIdAndActive() {
        String groupName = "name";
        Page<Tag> tags = mockTagPage();
        when(tagRepository.findAllByGroupNameAndActive(groupName, true, pageable)).thenReturn(tags);
        Page<Tag> resultedTags = tagService.findAllByGroupIdAndActive(groupName, true, pageable);
        assertEquals(2, resultedTags.getTotalElements());
    }

    @Test
    public void testDeleteTagById() {
        Integer tagId = 1;
        doNothing().when(tagRepository).deleteById(tagId);
        tagService.deleteTagById(tagId);
        verify(tagService, times(1)).deleteTagById(tagId);
    }

    @Test
    public void testAcceptTag() {
        Integer tagId = 1;
        Optional<Tag> optionalTag = mockTagOptional();
        Tag tag = optionalTag.get();
        when(tagRepository.findById(tagId)).thenReturn(optionalTag);
        when(tagRepository.save(tag)).thenReturn(tag);
        tagService.acceptTag(tagId);
        verify(tagService, times(1)).acceptTag(tagId);
    }

    @Test
    public void testDeleteTagByQuestionId() {
        doNothing().when(tagRepository).deleteAllByQuestionId(anyString());
        tagService.deleteTagsByQuestionId(anyString());
        verify(tagService, times(1)).deleteTagsByQuestionId(anyString());
    }

    private Page<Tag> mockTagPage() {
        List<Tag> tagList = new ArrayList<>();
        tagList.add(mockTag());
        tagList.add(mockTag());
        return new PageImpl<>(tagList);
    }

    private Tag mockTag() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("tag");
        return tag;
    }

    private Optional<Tag> mockTagOptional() {
        return Optional.of(mockTag());
    }
}
