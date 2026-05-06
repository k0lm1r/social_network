package com.kolmir.feed_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;


public interface PostService {
    public Page<PostResponse> getAll(Pageable pageable);
    public Page<PostResponse> getSortedByPopularity(Pageable pageable);
    public Page<PostResponse> getAllFromUser(Long userId, Pageable pageable);
    public Page<PostResponse> getFeedForUser(Long userId, Pageable pageable);
    public PostResponse getById(Long id);
    public PostResponse create(PostRequest request);
    public PostResponse update(Long id, PostRequest request);
    public void delete(Long id);
}
