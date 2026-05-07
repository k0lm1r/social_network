package com.kolmir.feed_service.openfeign.dto;

import java.util.Collection;


public record FollowListResponse (
    Integer count,
    Collection<Long> subscribersIds
) {}
