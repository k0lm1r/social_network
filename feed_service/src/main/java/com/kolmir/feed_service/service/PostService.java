package com.kolmir.feed_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;


public interface PostService {
    public Page<PostResponse> getAll(Pageable pageable);
    public Page<PostResponse> getAllFromUser(Long userId, Pageable pageable);
    public Page<PostResponse> getFeedForUser(Long userId, int pageNumber, int pageSize);
    public PostResponse getById(Long id);
    public PostResponse create(PostRequest request);
    public void updatePopularity(Long postId);
    public PostResponse update(Long id, PostRequest request);
    public Boolean isPostExists(Long postId);
    public void delete(Long id);
    public Boolean isCurrentUserOwner(Long postId);
}
