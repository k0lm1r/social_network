package com.kolmir.subscription_service.dto.reaction;

import com.kolmir.subscription_service.validation.ValidAction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddReactionRequest (
    @Positive(message = "{positive}")
    Long postId, 

    @NotNull(message = "{notnull}")
    @ValidAction(message = "{action}")
    String action
) {}
