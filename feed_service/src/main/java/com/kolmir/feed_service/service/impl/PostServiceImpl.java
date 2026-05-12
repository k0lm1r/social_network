package com.kolmir.feed_service.service.impl;

import static com.kolmir.feed_service.util.PostUtil.*;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.PostMapper;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;
import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;
import com.kolmir.feed_service.repository.PostRepository;
import com.kolmir.feed_service.service.CommentService;
import com.kolmir.feed_service.service.PostService;
import com.kolmir.security.provider.CurrentUserProvider;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentService commentService;
    private final CurrentUserProvider currentUserProvider;
    private final FollowingAndReactionsService followingAndReactionsService;
    
    @Override
    @Cacheable(cacheNames = ALL_POSTS_CACHE, keyGenerator = "pageKeyGenerator")
    public Page<PostResponse> getAll(Pageable pageable) {
        return postRepository.findAll(pageable)
            .map(postMapper::toResponse);
    }
    
    @Override
    @Cacheable(cacheNames = ALL_FROM_USER_CACHE, keyGenerator = "pageKeyGenerator")
    public Page<PostResponse> getAllFromUser(Long userId, Pageable pageable) {
        return postRepository.findAllByAuthorId(userId, pageable)
            .map(postMapper::toResponse);
    }

    @Override
    @Cacheable(cacheNames = FEEDS_CACHE, keyGenerator = "pageKeyGenerator")
    public Page<PostResponse> getFeedForUser(Long userId, Pageable pageable) {
        return postRepository.findByAuthorIdIn(
            followingAndReactionsService.getFollowingsIdsForUser(userId), 
            withSortByPopularity(pageable)
        ).map(postMapper::toResponse);
    }

    @Override
    @Cacheable(cacheNames = POST_CACHE, key = "#id")
    public PostResponse getById(Long id) {
        return postMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {FEEDS_CACHE, ALL_POSTS_CACHE, ALL_FROM_USERS_CACHE}, allEntries = true)
    public PostResponse create(PostRequest request) {
        Post post = postMapper.toPost(request);
        post.setAuthorId(currentUserProvider.getCurrentUserId());
        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = {FEEDS_CACHE, ALL_POSTS_CACHE, ALL_FROM_USERS_CACHE}, allEntries = true),
        @CacheEvict(cacheNames = POST_CACHE, key = "#id")
    })    
    public PostResponse update(Long id, PostRequest request) {
        Post post = findById(id);
        post.setText(request.text());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = {FEEDS_CACHE, ALL_POSTS_CACHE, ALL_FROM_USERS_CACHE}, allEntries = true),
        @CacheEvict(cacheNames = POST_CACHE, key = "#id")
    })
    public void delete(Long id) {
        postRepository.delete(findById(id));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = {FEEDS_CACHE, ALL_POSTS_CACHE, ALL_FROM_USERS_CACHE}, allEntries = true),
        @CacheEvict(cacheNames = POST_CACHE, key = "#postId")
    })
    public void updatePopularity(Long postId) {
        Post post = findById(postId);
        ReactionResponse reactions = followingAndReactionsService.getReactionsForPost(postId);
        
        post.setPopularity(calcPopularity(
            reactions.likeCount(), 
            reactions.dislikeCount(), 
            commentService.getCommentsCountForPost(postId)
        ));

        postMapper.toResponse(postRepository.save(post));
    }

    @Override
    public Boolean isPostExists(Long postId) {
        return postRepository.existsById(postId);
    }

    @Override
    public Boolean isCurrentUserOwner(Long postId) {
        return postRepository.existsByIdAndAuthorId(currentUserProvider.getCurrentUserId(), postId);
    }

    private Pageable withSortByPopularity(Pageable pageable) {
        Sort sortByPopularity = Sort.by(
            Sort.Order.desc(CREATED_AT_FIELD),
            Sort.Order.desc(POPULARITY_FIELD_NAME)
        );
        return PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sortByPopularity
        );
    }

    private Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_MESSAGE)
        );
    }

    private Double calcPopularity(Integer likesCount, Integer dislikesCount, Integer commentsCount) {
        return likesCount * 1.5 - dislikesCount + commentsCount * 2;
    }
}
