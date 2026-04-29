package com.kolmir.subscription_service.dto.event;

import com.kolmir.subscription_service.model.Action;


public record SubscriptionResponse (
    String id,
    Action action,
    Long userId,
    Long targetUserId
) implements InteractionEventResponse {}
