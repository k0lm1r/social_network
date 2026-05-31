package com.kolmir.subscription_service.dto.reaction;


public record ReactionResponse (
    Long postId,
    Integer likeCount,
    Integer dislikeCount
) {}
