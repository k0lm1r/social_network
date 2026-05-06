package com.kolmir.feed_service.service.impl;

import static com.kolmir.feed_service.util.PostUtil.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.PostMapper;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.repository.PostRepository;
import com.kolmir.feed_service.service.PostService;
import com.kolmir.security.provider.CurrentUserProvider;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CurrentUserProvider currentUserProvider;
    
    @Override
    public Page<PostResponse> getAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toResponse);
    }
    
    @Override
    public Page<PostResponse> getAllFromUser(Long userId, Pageable pageable) {
        return postRepository.findAllByAuthorId(userId, pageable)
        .map(postMapper::toResponse);
    }
    
    @Override
    public Page<PostResponse> getSortedByPopularity(Pageable pageable) {
        
    }

    @Override
    public Page<PostResponse> getFeedForUser(Long userId, Pageable pageable) {
    
    }

    @Override
    public PostResponse getById(Long id) {
        return postMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest request) {
        Post post = postMapper.toPost(request);
        post.setAuthorId(currentUserProvider.getCurrentUserId());
        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post post = findById(id);
        post.setText(request.text());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        postRepository.delete(findById(id));
    }

    private Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_MESSAGE)
        );
    }
}
