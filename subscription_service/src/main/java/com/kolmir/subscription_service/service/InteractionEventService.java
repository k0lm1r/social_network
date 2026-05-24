package com.kolmir.subscription_service.service;

import java.util.List;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.event.InteractionEventResponse;


public interface InteractionEventService {
    public List<InteractionEventResponse> getAll();
    public List<InteractionEventResponse> getAllByAction(String action);
    public InteractionEventResponse save(CreateInteractionEventRequest request);
    public InteractionEventResponse getEventById(String id);
    public InteractionEventResponse getReactionFromUser(Long userId, Long postId);
    public boolean userHasReaction(Long userId, Long postId, String action);
    public void delete(String id);
}
