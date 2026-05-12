package com.kolmir.feed_service.testutil;

import java.time.LocalDateTime;

import com.kolmir.feed_service.dto.comment.CommentCreateRequest;
import com.kolmir.feed_service.dto.comment.CommentResponse;
import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.model.Post;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentServiceImplTestObjectFactory {
    public static CommentCreateRequest createRequest(Long postId, String text) {
        return new CommentCreateRequest(postId, text);
    }

    public static Comment comment(Long id, Long authorId, Long postId, String text) {
        Post post = new Post();
        post.setId(postId);
        return new Comment(id, post, authorId, text, LocalDateTime.now());
    }

    public static CommentResponse response(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getAuthorId(),
            comment.getPost().getId(),
            comment.getText(),
            comment.getCreatedAt()
        );
    }
}
