package com.kolmir.subscription_service.dto.subscription;

public record SubscriptionLinkResponse (
    Long followerId,
    Long followingId
) {}
