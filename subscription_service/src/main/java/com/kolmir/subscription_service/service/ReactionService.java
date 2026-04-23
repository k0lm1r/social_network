package com.kolmir.subscription_service.service;

import java.util.Collection;
import java.util.Set;

import com.kolmir.subscription_service.dto.AddReactionRequest;
import com.kolmir.subscription_service.dto.DeleteReactionRequest;
import com.kolmir.subscription_service.dto.ReactionResponse;


public interface ReactionService {
    public ReactionResponse addReaction(AddReactionRequest request);
    public ReactionResponse deleteReaction(DeleteReactionRequest request);
    public Collection<ReactionResponse> getReactionsForAllPosts(Set<Long> postIds);
}
