package com.kolmir.subscription_service.factory;

import com.kolmir.subscription_service.dto.reaction.ReactionResponse;

import lombok.experimental.UtilityClass;


@UtilityClass
public class ReactionFactory {
    public static ReactionResponse createEmptyResponse(Long postId) {
        return new ReactionResponse(postId, 0, 0);
    }
}
