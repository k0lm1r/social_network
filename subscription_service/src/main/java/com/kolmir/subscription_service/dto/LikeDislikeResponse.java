package com.kolmir.subscription_service.dto;

import com.kolmir.subscription_service.model.Action;


public record LikeDislikeResponse (
    String id,
    Action action,
    Long userId,
    Long postId
) implements InteractionEventResponse {}