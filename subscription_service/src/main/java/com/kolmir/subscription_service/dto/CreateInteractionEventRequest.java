package com.kolmir.subscription_service.dto;

import com.kolmir.subscription_service.model.Action;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record CreateInteractionEventRequest (
    @NotNull(message = "{notnull}")
    String action,

    @NotNull(message = "{notnull}")
    @Positive(message = "{positive}")
    Long userId,

    Long targetUserId,
    Long postId
) {
    @AssertTrue(message = "{specific}")
    public boolean isValidEvent() {
        if (action.equals(Action.SUBSCRIBE.getName()) || action.equals(Action.UNSUBSCRIBE.getName()))
            return targetUserId != null && targetUserId > 0 && postId == null;
        if (action.equals(Action.LIKE.getName()) || action.equals(Action.DISLIKE.getName()))
            return postId != null && postId > 0 && targetUserId == null;
        return false;
    }
}