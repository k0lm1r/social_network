package com.kolmir.feed_service.openfeign.service;

import java.util.Collection;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;


public interface FollowingAndReactionsService {
    public Collection<Long> getFollowingsIdsForUser(Long userId); 
    public ReactionResponse getReactionsForPost(Long postId);
}
