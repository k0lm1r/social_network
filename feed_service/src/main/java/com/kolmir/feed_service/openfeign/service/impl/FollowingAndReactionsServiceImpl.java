package com.kolmir.feed_service.openfeign.service.impl;

import java.util.Collection;
import org.springframework.stereotype.Service;

import com.kolmir.feed_service.exception.ExternalServiceException;
import com.kolmir.feed_service.openfeign.SubscriptionClient;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;
import com.kolmir.feed_service.openfeign.service.FollowingAndReactionsService;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FollowingAndReactionsServiceImpl implements FollowingAndReactionsService {
    private final SubscriptionClient subscriptionClient;

    @Override
    public Collection<Long> getFollowingsIdsForUser(Long userId) {
        try {
            return subscriptionClient.getAllFollowingsForUser(userId).subscribersIds();
        } catch (FeignClientException e) {
            throw new ExternalServiceException(e.getMessage());
        }
    }

    @Override
    public ReactionResponse getReactionsForPost(Long postId) {
        try {
            return subscriptionClient.getReactionsForPosts(postId);
        } catch (FeignClientException e) {
            throw new ExternalServiceException(e.getMessage());
        }
    }
}
