package com.kolmir.subscription_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestConstants.*;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestObjectFactory.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.event.InteractionEventResponse;
import com.kolmir.subscription_service.exception.AlreadyExistsException;
import com.kolmir.subscription_service.exception.NotFoundException;
import com.kolmir.subscription_service.mapper.InteractionEventMapper;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.repository.InteractionEventRepository;

@ExtendWith(MockitoExtension.class)
class InteractionEventServiceImplTest {
    @Mock
    private InteractionEventRepository repository;

    @Mock
    private InteractionEventMapper mapper;

    @Mock
    private UserExistenceService userExistenceService;

    @InjectMocks
    private InteractionEventServiceImpl service;

    @Test
    void getAll_returnsMappedEvents() {
        var event = likeInteractionEvent();
        InteractionEventResponse response = likeDislikeResponse();

        when(repository.findAll()).thenReturn(List.of(event));
        when(mapper.toResponses(List.of(event))).thenReturn(List.of(response));

        assertThat(service.getAll()).containsExactly(response);
    }

    @Test
    void getAllByAction_returnsMappedEventsForAction() {
        var event = likeInteractionEvent();
        InteractionEventResponse response = likeDislikeResponse();

        when(repository.findByAction(Action.LIKE.getName())).thenReturn(List.of(event));
        when(mapper.toResponses(List.of(event))).thenReturn(List.of(response));

        assertThat(service.getAllByAction(Action.LIKE.getName())).containsExactly(response);
    }

    @Test
    void getEventById_throwsWhenEventDoesNotExist() {
        when(repository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEventById(EVENT_ID))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void save_persistsUniqueLikeEvent() {
        CreateInteractionEventRequest request = createLikeEventRequest();
        var event = likeInteractionEvent();
        InteractionEventResponse response = likeDislikeResponse();

        when(repository.existsByUserIdAndPostId(USER_ID, POST_ID)).thenReturn(false);
        when(mapper.toInteractionEvent(request)).thenReturn(event);
        when(repository.save(event)).thenReturn(event);
        when(mapper.toResponse(event)).thenReturn(response);

        assertThat(service.save(request)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(USER_ID);
    }

    @Test
    void save_rejectsDuplicateSubscriptionEvent() {
        CreateInteractionEventRequest request = createSubscribeEventRequest();

        when(repository.existsByUserIdAndTargetUserId(USER_ID, TARGET_USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.save(request))
            .isInstanceOf(AlreadyExistsException.class);
        verify(userExistenceService).validateUserExists(USER_ID);
    }

    @Test
    void getReactionFromUser_returnsMappedReactionEvent() {
        var event = likeInteractionEvent();
        InteractionEventResponse response = likeDislikeResponse();

        when(repository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(event));
        when(mapper.toResponse(event)).thenReturn(response);

        assertThat(service.getReacitonFromUser(USER_ID, POST_ID)).isEqualTo(response);
    }

    @Test
    void delete_removesEventById() {
        service.delete(EVENT_ID);

        verify(repository).deleteById(EVENT_ID);
    }

    @Test
    void userHasReaction_delegatesToRepository() {
        when(repository.existsByUserIdAndPostId(USER_ID, POST_ID)).thenReturn(true);

        assertThat(service.userHasReaction(USER_ID, POST_ID)).isTrue();
    }
}
