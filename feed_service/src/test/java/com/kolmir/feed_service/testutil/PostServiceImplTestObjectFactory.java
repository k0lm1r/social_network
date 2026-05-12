package com.kolmir.feed_service.testutil;

import java.time.LocalDateTime;
import java.util.List;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.model.Post;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostServiceImplTestObjectFactory {
    public static PostRequest request(String text) {
        return new PostRequest(text);
    }

    public static Post post(Long id, Long authorId, String text, Double popularity) {
        LocalDateTime now = LocalDateTime.now();
        return new Post(id, authorId, text, now, popularity, now, List.of());
    }

    public static PostResponse response(Post post) {
        return new PostResponse(post.getId(), post.getAuthorId(), post.getText(), post.getCreatedAt(), post.getUpdatedAt());
    }

    public static ReactionResponse reactions(Long postId, Integer likes, Integer dislikes) {
        return new ReactionResponse(postId, likes, dislikes);
    }
}
