package com.kolmir.feed_service.openfeign.dto;


public record ReactionResponse (
    Long postId,
    Integer likeCount,
    Integer dislikeCount
) {}
