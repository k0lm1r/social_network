package com.kolmir.feed_service.testutil.integration;

import java.time.LocalDateTime;
import java.util.List;

import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;

public final class FeedIntegrationTestObjectFactory {
    private FeedIntegrationTestObjectFactory() {
    }

    public static Post post(Long authorId, String text, Double popularity) {
        return new Post(
            null,
            authorId,
            text,
            popularity,
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of()
        );
    }

    public static Comment comment(Post post, Long authorId, String text) {
        return new Comment(
            null,
            post,
            authorId,
            text,
            LocalDateTime.now()
        );
    }

    public static ReactionResponse reactionResponse(Long postId, Integer likes, Integer dislikes) {
        return new ReactionResponse(postId, likes, dislikes);
    }
}
