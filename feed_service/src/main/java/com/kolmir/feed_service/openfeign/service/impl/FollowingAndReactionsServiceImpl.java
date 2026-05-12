package com.kolmir.feed_service.openfeign.service.impl;

import java.util.Collection;
import org.springframework.stereotype.Service;

import com.kolmir.feed_service.openfeign.SubscriptionClient;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;
import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FollowingAndReactionsServiceImpl implements FollowingAndReactionsService {
    private final SubscriptionClient subscriptionClient;

    @Override
    public Collection<Long> getFollowingsIdsForUser(Long userId) {
        return subscriptionClient.getAllFollowingsForUser(userId).subscribersIds();
    }

    @Override
    public ReactionResponse getReactionsForPost(Long postId) {
        return subscriptionClient.getReactionsForPosts(postId);
    }
}
