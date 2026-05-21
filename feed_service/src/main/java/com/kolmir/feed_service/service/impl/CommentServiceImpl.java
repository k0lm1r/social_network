package com.kolmir.feed_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.feed_service.dto.comment.*;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.CommentMapper;
import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.repository.CommentRepository;
import com.kolmir.feed_service.service.CommentService;
import static com.kolmir.feed_service.util.CommentUtil.*;
import com.kolmir.security.provider.CurrentUserProvider;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getAllCommentsForPost(Long postId, Pageable pageable) {
        return commentRepository.findAllByPostId(postId, pageable)
            .map(commentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getAllFromUserToPost(Long userId, Long postId, Pageable pageable) {
        return commentRepository.findAllByAuthorIdAndPostId(userId, postId, pageable)
            .map(commentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getById(Long commentId) {
        return commentMapper.toResponse(findById(commentId));
    }

    @Override
    public CommentResponse create(CommentCreateRequest request) {
        Comment comment = commentMapper.toComment(request);
        comment.setAuthorId(currentUserProvider.getCurrentUserId());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public Long getCommentsCountForPost(Long postId) {
        return commentRepository.findCountByPostId(postId);
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.delete(findById(commentId));
    }

    @Override
    public Boolean isCurrentUserOwner(Long commentId) {
        return commentRepository.existsByIdAndAuthorId(commentId, currentUserProvider.getCurrentUserId());
    }

    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_MESSAGE)
        );
    }
}
