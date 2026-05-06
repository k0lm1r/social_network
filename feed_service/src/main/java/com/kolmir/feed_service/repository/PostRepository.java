package com.kolmir.feed_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.feed_service.model.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    public Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
}
