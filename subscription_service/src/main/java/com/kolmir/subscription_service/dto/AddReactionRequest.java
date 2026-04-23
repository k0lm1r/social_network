package com.kolmir.subscription_service.dto;


public record AddReactionRequest (
    Long postId, 
    Long userId, 
    String action
) {}
