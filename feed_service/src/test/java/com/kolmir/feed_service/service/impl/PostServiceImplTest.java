package com.kolmir.feed_service.service.impl;

import static com.kolmir.feed_service.testutil.PostServiceImplTestConstants.*;
import static com.kolmir.feed_service.testutil.PostServiceImplTestObjectFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.PostMapper;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;
import com.kolmir.feed_service.repository.PostRepository;
import com.kolmir.feed_service.service.CommentService;
import com.kolmir.security.provider.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private CommentService commentService;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private FollowingAndReactionsService followingAndReactionsService;
    @InjectMocks
    private PostServiceImpl service;

    @Test
    void getAll_returnsMappedPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Post entity = post(FEED_POST_ID, FOLLOWING_ID, FEED_POST_TEXT, FEED_POPULARITY);
        PostResponse mapped = response(entity);
        when(postRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(postMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getAll(pageable).getContent()).containsExactly(mapped);
    }

    @Test
    void getAllFromUser_returnsMappedPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Post entity = post(FEED_POST_ID, USER_ID, FEED_POST_TEXT, FEED_POPULARITY);
        PostResponse mapped = response(entity);
        when(postRepository.findAllByAuthorId(USER_ID, pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(postMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getAllFromUser(USER_ID, pageable).getContent()).containsExactly(mapped);
    }

    @Test
    void getById_returnsMappedPost() {
        Post entity = post(POST_ID, AUTHOR_ID, POST_TEXT, DEFAULT_POPULARITY);
        PostResponse mapped = response(entity);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(entity));
        when(postMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getById(POST_ID)).isEqualTo(mapped);
    }

    @Test
    void create_setsCurrentUserAsAuthorAndSaves() {
        PostRequest req = request(NEW_POST_TEXT);
        Post unsaved = post(null, null, NEW_POST_TEXT, DEFAULT_POPULARITY);
        Post saved = post(CREATED_POST_ID, CURRENT_USER_ID, NEW_POST_TEXT, DEFAULT_POPULARITY);
        PostResponse mapped = response(saved);

        when(postMapper.toPost(req)).thenReturn(unsaved);
        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(postRepository.save(unsaved)).thenReturn(saved);
        when(postMapper.toResponse(saved)).thenReturn(mapped);

        assertThat(service.create(req)).isEqualTo(mapped);
        assertThat(unsaved.getAuthorId()).isEqualTo(CURRENT_USER_ID);
    }

    @Test
    void update_changesTextAndSaves() {
        PostRequest req = request(UPDATED_POST_TEXT);
        Post entity = post(POST_ID, AUTHOR_ID, POST_TEXT, DEFAULT_POPULARITY);
        PostResponse mapped = response(entity);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(entity));
        when(postRepository.save(entity)).thenReturn(entity);
        when(postMapper.toResponse(entity)).thenReturn(mapped);

        service.update(POST_ID, req);

        assertThat(entity.getText()).isEqualTo(UPDATED_POST_TEXT);
    }

    @Test
    void updatePopularity_recalculatesAndSavesPopularity() {
        Post entity = post(POST_ID, AUTHOR_ID, POST_TEXT, DEFAULT_POPULARITY);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(entity));
        when(followingAndReactionsService.getReactionsForPost(POST_ID)).thenReturn(reactions(POST_ID, LIKE_COUNT, DISLIKE_COUNT));
        when(commentService.getCommentsCountForPost(POST_ID)).thenReturn(COMMENTS_COUNT);
        when(postRepository.save(entity)).thenReturn(entity);
        when(postMapper.toResponse(entity)).thenReturn(response(entity));

        service.updatePopularity(POST_ID);

        assertThat(entity.getPopularity()).isEqualTo(EXPECTED_POPULARITY);
        verify(postRepository).save(entity);
    }

    @Test
    void getFeedForUser_usesFollowingsAndMapsPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Post entity = post(FEED_POST_ID, FOLLOWING_ID, FEED_POST_TEXT, FEED_POPULARITY);
        PostResponse mapped = response(entity);
        Page<Post> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(followingAndReactionsService.getFollowingsIdsForUser(USER_ID)).thenReturn(List.of(FOLLOWING_ID));
        when(postRepository.findByAuthorIdIn(any(), any(Pageable.class))).thenReturn(page);
        when(postMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getFeedForUser(USER_ID, pageable).getContent()).containsExactly(mapped);
    }

    @Test
    void delete_removesPostWhenFound() {
        Post entity = post(POST_ID, AUTHOR_ID, POST_TEXT, DEFAULT_POPULARITY);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(entity));

        service.delete(POST_ID);

        verify(postRepository).delete(entity);
    }

    @Test
    void delete_throwsWhenPostMissing() {
        when(postRepository.findById(MISSING_POST_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(MISSING_POST_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void isPostExists_delegatesToRepository() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        assertThat(service.isPostExists(POST_ID)).isTrue();
    }
}
