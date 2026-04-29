package com.kolmir.subscription_service.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.subscription_service.dto.event.LikeDislikeResponse;
import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.factory.ReactionFactory;
import com.kolmir.subscription_service.mapper.ReactionMapper;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.Reaction;
import com.kolmir.subscription_service.repository.ReactionRepository;
import com.kolmir.subscription_service.security.SecurityUtils;
import com.kolmir.subscription_service.service.InteractionEventService;
import com.kolmir.subscription_service.service.ReactionService;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository repository;
    private final ReactionMapper mapper;
    private final InteractionEventService interactionEventService;

    @Override
    public ReactionResponse addReaction(AddReactionRequest request) {
        interactionEventService.save(mapper.toCreateEventRequest(request));
        return changeReactionsCount(request.postId(), Action.valueOf(request.action()), 1);
    }

    @Override
    public ReactionResponse deleteReaction(Long postId) {
        LikeDislikeResponse event = (LikeDislikeResponse)interactionEventService
            .getReacitonFromUser(SecurityUtils.getCurrentUser().id(), postId);
        return changeReactionsCount(postId, event.action(), -1);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Collection<ReactionResponse> getReactionsForAllPosts(Set<Long> postIds) {
        Map<Long, ReactionResponse> posts = repository.findByPostIdIn(postIds).stream()
            .collect(Collectors.toMap(Reaction::getPostId, mapper::toResponse));

        for (var postId : postIds) {
            if (!posts.containsKey(postId))
                posts.put(postId, ReactionFactory.createEmptyResponse(postId));
        }

        return posts.values();
    }

    private Reaction getReactionByPostId(Long postId) {
        Reaction reaction = repository.findByPostId(postId).orElse(new Reaction());
        if (reaction.getPostId() == null)
            reaction.setPostId(postId);
        return reaction;
    }

    private ReactionResponse changeReactionsCount(Long postId, Action action, int delta) {
        Reaction reaction = getReactionByPostId(postId);
        if (action.equals(Action.LIKE))
            reaction.setLikeCount(reaction.getLikeCount() + delta);
        else 
            reaction.setDislikeCount(reaction.getDislikeCount() + delta);
        return mapper.toResponse(repository.save(reaction));
    }
}
