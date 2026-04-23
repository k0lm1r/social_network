package com.kolmir.subscription_service.service;

import java.util.List;

import com.kolmir.subscription_service.dto.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.InteractionEventResponse;


public interface InteractionEventService {
    public List<InteractionEventResponse> getAll();
    public List<InteractionEventResponse> getAllByAction(String action);
    public InteractionEventResponse save(CreateInteractionEventRequest request);
    public InteractionEventResponse getEventById(String id);
    public InteractionEventResponse getReacitonFromUser(Long userId, Long postId);
    public void delete(String id);
}
