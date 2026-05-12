package com.kolmir.feed_service.service.impl;

import static com.kolmir.feed_service.testutil.CommentServiceImplTestConstants.*;
import static com.kolmir.feed_service.testutil.CommentServiceImplTestObjectFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.kolmir.feed_service.dto.comment.CommentCreateRequest;
import com.kolmir.feed_service.dto.comment.CommentResponse;
import com.kolmir.feed_service.exception.NotFoundException;
import com.kolmir.feed_service.mapper.CommentMapper;
import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.repository.CommentRepository;
import com.kolmir.security.provider.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private CommentServiceImpl service;

    @Test
    void getAllCommentsForPost_returnsMappedPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Comment entity = comment(COMMENT_ID, USER_ID, POST_ID, COMMENT_TEXT);
        CommentResponse mapped = response(entity);
        Page<Comment> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(commentRepository.findAllByPostId(POST_ID, pageable)).thenReturn(page);
        when(commentMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getAllCommentsForPost(POST_ID, pageable).getContent()).containsExactly(mapped);
    }

    @Test
    void getAllFromUserToPost_returnsMappedPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Comment entity = comment(COMMENT_ID, USER_ID, POST_ID, COMMENT_TEXT);
        CommentResponse mapped = response(entity);
        Page<Comment> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(commentRepository.findAllByAuthorIdAndPostId(USER_ID, POST_ID, pageable)).thenReturn(page);
        when(commentMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getAllFromUserToPost(USER_ID, POST_ID, pageable).getContent()).containsExactly(mapped);
    }

    @Test
    void getById_returnsMappedComment() {
        Comment entity = comment(COMMENT_ID, USER_ID, POST_ID, COMMENT_TEXT);
        CommentResponse mapped = response(entity);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(entity));
        when(commentMapper.toResponse(entity)).thenReturn(mapped);

        assertThat(service.getById(COMMENT_ID)).isEqualTo(mapped);
    }

    @Test
    void create_setsCurrentUserAndSavesComment() {
        CommentCreateRequest request = createRequest(POST_ID, COMMENT_TEXT);
        Comment unsaved = comment(null, null, POST_ID, COMMENT_TEXT);
        Comment saved = comment(COMMENT_ID, USER_ID, POST_ID, COMMENT_TEXT);
        CommentResponse mapped = response(saved);

        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(commentMapper.toComment(request)).thenReturn(unsaved);
        when(commentRepository.save(unsaved)).thenReturn(saved);
        when(commentMapper.toResponse(saved)).thenReturn(mapped);

        assertThat(service.create(request)).isEqualTo(mapped);
        assertThat(unsaved.getAuthorId()).isEqualTo(USER_ID);
    }

    @Test
    void getById_throwsWhenCommentMissing() {
        when(commentRepository.findById(MISSING_COMMENT_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(MISSING_COMMENT_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_deletesCommentWhenFound() {
        Comment saved = comment(COMMENT_ID, USER_ID, POST_ID, COMMENT_TEXT);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(saved));

        service.delete(COMMENT_ID);

        verify(commentRepository).delete(saved);
    }

    @Test
    void getCommentsCountForPost_delegatesToRepository() {
        when(commentRepository.findCountByPostId(POST_ID)).thenReturn(COMMENTS_COUNT);
        assertThat(service.getCommentsCountForPost(POST_ID)).isEqualTo(COMMENTS_COUNT);
    }
}
