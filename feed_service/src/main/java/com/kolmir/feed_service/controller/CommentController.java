package com.kolmir.feed_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.feed_service.dto.comment.CommentCreateRequest;
import com.kolmir.feed_service.dto.comment.CommentResponse;
import com.kolmir.feed_service.dto.comment.CommentsCountResponse;
import com.kolmir.feed_service.service.CommentService;
import static com.kolmir.feed_service.util.CommentUtil.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(COMMENT_MAIN_URL)
public class CommentController {
    private final CommentService commentService;

    @GetMapping(COMMENTS_FOR_POST)
    public ResponseEntity<Page<CommentResponse>> getAllForPost(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllCommentsForPost(postId, pageable));
    }
    
    @GetMapping(COMMENT_ID_URL)
    public ResponseEntity<CommentResponse> getById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getById(commentId));
    }

    @GetMapping(COMMENTS_COUNT_FOR_POST)
    public ResponseEntity<CommentsCountResponse> getCountForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsCountForPost(postId));
    }

    @GetMapping(COMMENTS_BY_USER)
    public ResponseEntity<Page<CommentResponse>> getAllFromUserForPost(@PathVariable Long postId, @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllFromUserToPost(userId, postId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.create(request));
    }

    @DeleteMapping(COMMENT_ID_URL)
    @PreAuthorize("hasAnyRole('ADMIN', 'MAIN_ADMIN') || @commentService.isCurrentUserOwner(#commentId)")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
