package com.kolmir.feed_service.testutil.bdd;

import java.time.LocalDateTime;
import java.util.List;

import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.model.Post;

import lombok.experimental.UtilityClass;


@UtilityClass
public class FeedBddObjectFactory {
    public static Post post(Long id, Long authorId, String text) {
        return new Post(id, authorId, text, 0.0, LocalDateTime.now(), null, List.of());
    }

    public static PostResponse postResponse(Post post) {
        return new PostResponse(post.getId(), post.getAuthorId(), post.getText(), post.getCreatedAt(), post.getUpdatedAt());
    }

    public static Comment comment(Long id, Long authorId, Post post, String text) {
        return new Comment(id, post, authorId, text, LocalDateTime.now());
    }
}
