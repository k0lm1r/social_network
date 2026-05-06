package com.kolmir.subscription_service.dto.reaction;

import com.kolmir.subscription_service.validation.ValidAction;

import jakarta.validation.constraints.NotNull;


public record AddReactionRequest (
    @ValidAction
    @NotNull(message = "{notnull}")
    String action
) {}
