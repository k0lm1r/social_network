package com.kolmir.subscription_service.dto.reaction;

import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.validation.ValidAction;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;


public record AddReactionRequest (
    @ValidAction
    @NotNull(message = "{notnull}")
    String action
) {
    @AssertTrue(message = "{reaction}")
    public boolean isValidEvent() {
        return action.equals(Action.LIKE.getName()) ||
        action.equals(Action.DISLIKE.getName());
    }    
}
