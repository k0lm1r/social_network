package com.kolmir.subscription_service.dto.subscription;

import java.util.Collection;


public record FollowListResponse (
    Integer count,
    Collection<Long> subscribersIds
) {}
