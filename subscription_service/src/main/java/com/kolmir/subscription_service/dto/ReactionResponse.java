package com.kolmir.subscription_service.dto;


public record ReactionResponse (
    Long postId,
    Integer likeCount,
    Integer dislikeCount
) {}
