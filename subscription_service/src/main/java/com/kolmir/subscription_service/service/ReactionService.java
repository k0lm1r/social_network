package com.kolmir.subscription_service.service;

import java.util.Collection;
import java.util.Set;

import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;


public interface ReactionService {
    public ReactionResponse addReaction(AddReactionRequest request, Long postId);
    public ReactionResponse deleteReaction(Long postId);
    public Collection<ReactionResponse> getReactionsForAllPosts(Set<Long> postIds);
}
