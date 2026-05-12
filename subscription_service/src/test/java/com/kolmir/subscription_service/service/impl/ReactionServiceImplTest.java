package com.kolmir.subscription_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestConstants.*;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestObjectFactory.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.security.provider.CurrentUserProvider;
import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.mapper.ReactionMapper;
import com.kolmir.subscription_service.model.Reaction;
import com.kolmir.subscription_service.openfeign.service.PostService;
import com.kolmir.subscription_service.repository.ReactionRepository;
import com.kolmir.subscription_service.service.InteractionEventService;

@ExtendWith(MockitoExtension.class)
class ReactionServiceImplTest {
    @Mock
    private ReactionRepository repository;

    @Mock
    private ReactionMapper mapper;

    @Mock
    private InteractionEventService interactionEventService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private PostService postService;

    @InjectMocks
    private ReactionServiceImpl service;

    @Test
    void addReaction_createsReactionCounterWhenPostHasNoCounters() {
        AddReactionRequest request = addLikeReactionRequest();
        CreateInteractionEventRequest eventRequest = createLikeEventRequest();

        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(postService.isPostExists(POST_ID)).thenReturn(true);
        when(interactionEventService.userHasReaction(USER_ID, POST_ID)).thenReturn(false);
        when(mapper.toCreateEventRequest(request, POST_ID, USER_ID)).thenReturn(eventRequest);
        when(repository.findByPostId(POST_ID)).thenReturn(Optional.empty());
        when(repository.save(any(Reaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Reaction.class))).thenAnswer(invocation -> toResponse(invocation.getArgument(0)));

        ReactionResponse response = service.addReaction(request, POST_ID);

        assertThat(response).isEqualTo(reactionResponse(POST_ID, 1, 0));
        verify(interactionEventService).save(eventRequest);
    }

    @Test
    void addReaction_removesPreviousUserReactionBeforeAddingNewOne() {
        AddReactionRequest request = addLikeReactionRequest();
        Reaction reaction = existingReaction();
        CreateInteractionEventRequest eventRequest = createLikeEventRequest();

        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(postService.isPostExists(POST_ID)).thenReturn(true);
        when(interactionEventService.userHasReaction(USER_ID, POST_ID)).thenReturn(true);
        when(interactionEventService.getReacitonFromUser(USER_ID, POST_ID))
            .thenReturn(previousDislikeResponse());
        when(mapper.toCreateEventRequest(request, POST_ID, USER_ID)).thenReturn(eventRequest);
        when(repository.findByPostId(POST_ID)).thenReturn(Optional.of(reaction));
        when(repository.save(any(Reaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Reaction.class))).thenAnswer(invocation -> toResponse(invocation.getArgument(0)));

        ReactionResponse response = service.addReaction(request, POST_ID);

        assertThat(response).isEqualTo(reactionResponse(POST_ID, 3, 0));
        verify(interactionEventService).delete(EVENT_ID);
        verify(interactionEventService).save(eventRequest);
    }

    @Test
    void deleteReaction_doesNotAllowCounterToBecomeNegative() {
        Reaction reaction = emptyReaction();

        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(postService.isPostExists(POST_ID)).thenReturn(true);
        when(interactionEventService.getReacitonFromUser(USER_ID, POST_ID))
            .thenReturn(likeDislikeResponse());
        when(repository.findByPostId(POST_ID)).thenReturn(Optional.of(reaction));
        when(repository.save(any(Reaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Reaction.class))).thenAnswer(invocation -> toResponse(invocation.getArgument(0)));

        ReactionResponse response = service.deleteReaction(POST_ID);

        assertThat(response).isEqualTo(reactionResponse(POST_ID, 0, 0));
        verify(interactionEventService).delete(EVENT_ID);
    }

    @Test
    void getReactionsForAllPosts_addsEmptyResponsesForMissingPosts() {
        Reaction existing = firstPostReaction();

        when(repository.findByPostIdIn(Set.of(FIRST_POST_ID, SECOND_POST_ID))).thenReturn(List.of(existing));
        when(mapper.toResponse(existing)).thenReturn(reactionResponse(FIRST_POST_ID, 4, 2));

        Collection<ReactionResponse> responses = service.getReactionsForAllPosts(
            Set.of(FIRST_POST_ID, SECOND_POST_ID)
        );

        assertThat(responses).containsExactlyInAnyOrder(
            reactionResponse(FIRST_POST_ID, 4, 2),
            reactionResponse(SECOND_POST_ID, 0, 0)
        );
    }

    private ReactionResponse toResponse(Reaction reaction) {
        return reactionResponse(reaction.getPostId(), reaction.getLikeCount(), reaction.getDislikeCount());
    }
}
