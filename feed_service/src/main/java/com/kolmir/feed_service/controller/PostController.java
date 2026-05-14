package com.kolmir.feed_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.service.PostService;

import static com.kolmir.feed_service.util.PostUtil.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(POST_MAIN_URL)
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(postService.getAll(pageable));
    }
    
    @GetMapping(FROM_USER_URL)
    public ResponseEntity<Page<PostResponse>> getAllFromUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(postService.getAllFromUser(userId, pageable));
    }

    @GetMapping(FEED_URL)
    public ResponseEntity<Page<PostResponse>> getFeed(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(postService.getFeedForUser(userId, pageable));
    }

    @GetMapping(POST_ID_URL)
    public ResponseEntity<PostResponse> getById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getById(postId));
    }

    @GetMapping(EXISTING_URL)
    public ResponseEntity<Boolean> isPostExists(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.isPostExists(postId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(postService.create(request));
    }
    
    @DeleteMapping(POST_ID_URL)
    @PreAuthorize("hasAnyRole('ADMIN', 'MAIN_ADMIN') || @postService.isCurrentUserOwner(#postId)")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(POST_ID_URL)
    @PreAuthorize("hasAnyRole('ADMIN', 'MAIN_ADMIN') || @postService.isCurrentUserOwner(#postId)")
    public ResponseEntity<PostResponse> update(@PathVariable Long postId, @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.update(postId, request));
    }

    @PatchMapping(POPULARITY_URL)
    public ResponseEntity<Void> updatePopularity(@PathVariable Long postId) {
        postService.updatePopularity(postId);
        return ResponseEntity.ok().build();
    }
}
