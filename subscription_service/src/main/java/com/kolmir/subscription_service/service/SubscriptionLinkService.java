package com.kolmir.subscription_service.service;

import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;

public interface SubscriptionLinkService {
    public SubscriptionLinkResponse follow(Long followingId);
    public void unfollow (Long followingId);
    public FollowCountResponse getFollowersCountForUser(Long userId);
    public FollowListResponse getFollowersForUser(Long userId);
    public FollowCountResponse getFollowingsCountForUser(Long userId);
    public FollowListResponse getFollowingsForUser(Long userId);
    public Boolean isUserFollower(Long userId, Long followingId);
}