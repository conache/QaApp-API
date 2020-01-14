package com.project.qa.repository;

import com.project.qa.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findById(Integer id);
}
