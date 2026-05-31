package com.kolmir.subscription_service.testutil.integration;

import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.model.Action;

public final class SubscriptionIntegrationTestObjectFactory {
    private SubscriptionIntegrationTestObjectFactory() {
    }

    public static AddReactionRequest addLikeReactionRequest() {
        return new AddReactionRequest(Action.LIKE.name());
    }
}
