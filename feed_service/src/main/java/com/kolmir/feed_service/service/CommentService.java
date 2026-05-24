package com.kolmir.feed_service.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kolmir.feed_service.dto.comment.*;


public interface CommentService {
    public Page<CommentResponse> getAllCommentsForPost(Long postId, Pageable pageable);
    public CommentsCountResponse getCommentsCountForPost(Long postId);
    public Page<CommentResponse> getAllFromUserToPost(Long userId, Long postId, Pageable pageable);
    public CommentResponse getById(Long commentId);
    public CommentResponse create(CommentCreateRequest request);
    public void delete(Long commentId);
    public Boolean isCurrentUserOwner(Long commentId);
}
