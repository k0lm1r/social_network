package com.kolmir.subscription_service.dto;


public record DeleteReactionRequest (
    Long postId,
    Long userId
) {}
