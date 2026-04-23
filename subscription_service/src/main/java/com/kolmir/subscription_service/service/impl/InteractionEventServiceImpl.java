package com.kolmir.subscription_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.subscription_service.dto.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.InteractionEventResponse;
import com.kolmir.subscription_service.exception.AlreadyExistsException;
import com.kolmir.subscription_service.mapper.InteractionEventMapper;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.InteractionEvent;
import com.kolmir.subscription_service.repository.InteractionEventRepository;
import com.kolmir.subscription_service.service.InteractionEventService;
import static com.kolmir.subscription_service.util.InteractionEventUtil.*;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class InteractionEventServiceImpl implements InteractionEventService {
    private final InteractionEventRepository repository;
    private final InteractionEventMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<InteractionEventResponse> getAll() {
        return mapper.toResponses(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionEventResponse> getAllByAction(String action) {
        return mapper.toResponses(repository.findByAction(action));
    }

    @Override
    @Transactional(readOnly = true)
    public InteractionEventResponse getEventById(String id) {
        InteractionEvent event = repository.findById(id).orElseThrow(
            () -> new NotFoundException(EVENT_NOT_FOUND_MESSAGE)
        );
        return mapper.toResponse(event);
    }

    @Override
    public InteractionEventResponse save(CreateInteractionEventRequest request) {
        if (isEventNotUnique(request))
            throw new AlreadyExistsException(EVENT_ALREADY_EXISTS_MESSAGE);
        return mapper.toResponse(repository.save(mapper.toInteractionEvent(request)));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public InteractionEventResponse getReacitonFromUser(Long userId, Long postId) {
        InteractionEvent event = repository.findByPostIdAndUserId(postId, userId).orElseThrow(
            () -> new NotFoundException(EVENT_NOT_FOUND_MESSAGE)
        );
        return mapper.toResponse(event);
    }
    
    private boolean isEventNotUnique(CreateInteractionEventRequest request) {
        return switch(Action.valueOf(request.action())) {
            case LIKE, DISLIKE -> 
                repository.existsByUserIdAndPostId(request.userId(), request.postId());
            case SUBSCRIBE, UNSUBSCRIBE -> 
                repository.existsByUserIdAndTargetUserId(request.userId(), request.targetUserId());
        };
    }
}
