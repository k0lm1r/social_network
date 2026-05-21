package com.kolmir.subscription_service.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.mockito.Mockito;

import com.kolmir.security.provider.CurrentUserProvider;
import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.exception.AlreadyExistsException;
import com.kolmir.subscription_service.exception.ExternalServiceException;
import com.kolmir.subscription_service.exception.NotFoundException;
import com.kolmir.subscription_service.mapper.InteractionEventMapper;
import com.kolmir.subscription_service.mapper.ReactionMapper;
import com.kolmir.subscription_service.mapper.SubscriptionLinkMapper;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.InteractionEvent;
import com.kolmir.subscription_service.model.Reaction;
import com.kolmir.subscription_service.model.SubscriptionLink;
import com.kolmir.subscription_service.openfeign.PostClient;
import com.kolmir.subscription_service.openfeign.UserClient;
import com.kolmir.subscription_service.openfeign.service.PostService;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.openfeign.service.impl.PostServiceImpl;
import com.kolmir.subscription_service.openfeign.service.impl.UserExistenceServiceImpl;
import com.kolmir.subscription_service.repository.InteractionEventRepository;
import com.kolmir.subscription_service.repository.ReactionRepository;
import com.kolmir.subscription_service.repository.SubscriptionLinkRepository;
import com.kolmir.subscription_service.service.InteractionEventService;
import com.kolmir.subscription_service.service.impl.InteractionEventServiceImpl;
import com.kolmir.subscription_service.service.impl.ReactionServiceImpl;
import com.kolmir.subscription_service.service.impl.SubscriptionLinkServiceImpl;
import com.kolmir.subscription_service.testutil.bdd.SubscriptionBddObjectFactory;
import com.kolmir.subscription_service.testutil.bdd.SubscriptionBddTestConstants;

import feign.FeignException.FeignClientException;
import feign.RetryableException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class SubscriptionServiceSteps {
    private final SubscriptionLinkRepository subscriptionRepository = Mockito.mock(SubscriptionLinkRepository.class);
    private final SubscriptionLinkMapper subscriptionMapper = Mockito.mock(SubscriptionLinkMapper.class);
    private final UserExistenceService userExistenceService = Mockito.mock(UserExistenceService.class);
    private final CurrentUserProvider currentUserProvider = Mockito.mock(CurrentUserProvider.class);
    private final SubscriptionLinkServiceImpl subscriptionService = new SubscriptionLinkServiceImpl(
        subscriptionRepository,
        subscriptionMapper,
        userExistenceService,
        currentUserProvider
    );

    private final ReactionRepository reactionRepository = Mockito.mock(ReactionRepository.class);
    private final ReactionMapper reactionMapper = Mockito.mock(ReactionMapper.class);
    private final InteractionEventService interactionEventService = Mockito.mock(InteractionEventService.class);
    private final PostService postService = Mockito.mock(PostService.class);
    private final ReactionServiceImpl reactionService = new ReactionServiceImpl(
        reactionRepository,
        reactionMapper,
        interactionEventService,
        currentUserProvider,
        postService
    );

    private final InteractionEventRepository interactionEventRepository = Mockito.mock(InteractionEventRepository.class);
    private final InteractionEventMapper interactionEventMapper = Mockito.mock(InteractionEventMapper.class);
    private final UserExistenceService interactionUserExistenceService = Mockito.mock(UserExistenceService.class);
    private final InteractionEventServiceImpl interactionEventServiceImpl = new InteractionEventServiceImpl(
        interactionEventRepository,
        interactionEventMapper,
        interactionUserExistenceService
    );

    private final UserClient userClient = Mockito.mock(UserClient.class);
    private final UserExistenceServiceImpl userExistenceServiceImpl = new UserExistenceServiceImpl(userClient);

    private final PostClient postClient = Mockito.mock(PostClient.class);
    private final PostServiceImpl postServiceImpl = new PostServiceImpl(postClient);

    private Throwable thrown;
    private SubscriptionLinkResponse followResponse;
    private Collection<ReactionResponse> reactionResponses;
    private ReactionResponse singleReactionResponse;
    private com.kolmir.subscription_service.dto.subscription.FollowListResponse followersResponse;
    private com.kolmir.subscription_service.dto.subscription.FollowListResponse followingsResponse;
    private com.kolmir.subscription_service.dto.subscription.FollowCountResponse followersCount;
    private com.kolmir.subscription_service.dto.subscription.FollowCountResponse followingsCount;

    @Given("current user is configured in subscription service")
    public void currentUserIsConfiguredInSubscriptionService() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(SubscriptionBddTestConstants.USER_ID);
    }

    @Given("target user exists in identity service")
    public void targetUserExistsInIdentityService() {
        Mockito.doNothing().when(userExistenceService).validateUserExists(any(Long.class));
    }

    @When("the user follows another target user")
    public void theUserFollowsAnotherTargetUser() {
        SubscriptionLink saved = SubscriptionBddObjectFactory.subscriptionLink(
            SubscriptionBddTestConstants.SUBSCRIPTION_ID,
            SubscriptionBddTestConstants.USER_ID,
            SubscriptionBddTestConstants.TARGET_USER_ID
        );
        when(subscriptionRepository.save(any(SubscriptionLink.class))).thenReturn(saved);
        when(subscriptionMapper.toSubscriptionLink(
            eq(SubscriptionBddTestConstants.TARGET_USER_ID),
            eq(SubscriptionBddTestConstants.USER_ID)
        )).thenReturn(saved);
        when(subscriptionMapper.toSubscriptionLinkResponse(any(SubscriptionLink.class)))
            .thenReturn(new SubscriptionLinkResponse(SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.TARGET_USER_ID));
        followResponse = subscriptionService.follow(SubscriptionBddTestConstants.TARGET_USER_ID);
    }

    @Then("subscription link is created")
    public void subscriptionLinkIsCreated() {
        assertEquals(SubscriptionBddTestConstants.USER_ID, followResponse.followerId());
        assertEquals(SubscriptionBddTestConstants.TARGET_USER_ID, followResponse.followingId());
    }

    @When("the user follows themselves")
    public void theUserFollowsThemselves() {
        thrown = assertThrows(IllegalArgumentException.class,
            () -> subscriptionService.follow(SubscriptionBddTestConstants.USER_ID));
    }

    @Then("follow operation fails with business error")
    public void followOperationFailsWithBusinessError() {
        assertEquals(IllegalArgumentException.class, thrown.getClass());
    }

    @Given("subscription link exists for current user and target")
    public void subscriptionLinkExistsForCurrentUserAndTarget() {
        when(subscriptionRepository.findByFollowerIdAndFollowingId(
            SubscriptionBddTestConstants.USER_ID,
            SubscriptionBddTestConstants.TARGET_USER_ID
        )).thenReturn(Optional.of(
            SubscriptionBddObjectFactory.subscriptionLink(
                SubscriptionBddTestConstants.SUBSCRIPTION_ID,
                SubscriptionBddTestConstants.USER_ID,
                SubscriptionBddTestConstants.TARGET_USER_ID
            )
        ));
    }

    @When("the user unfollows target user")
    public void theUserUnfollowsTargetUser() {
        try {
            subscriptionService.unfollow(SubscriptionBddTestConstants.TARGET_USER_ID);
        } catch (Throwable e) {
            thrown = e;
        }
    }

    @Then("subscription link is deleted")
    public void subscriptionLinkIsDeleted() {
        verify(subscriptionRepository).delete(any(SubscriptionLink.class));
    }

    @Given("subscription link does not exist for current user and target")
    public void subscriptionLinkDoesNotExistForCurrentUserAndTarget() {
        when(subscriptionRepository.findByFollowerIdAndFollowingId(
            SubscriptionBddTestConstants.USER_ID,
            SubscriptionBddTestConstants.TARGET_USER_ID
        )).thenReturn(Optional.empty());
    }

    @Then("unfollow operation fails with not found error")
    public void unfollowOperationFailsWithNotFoundError() {
        assertEquals(NotFoundException.class, thrown.getClass());
    }

    @Given("repository has followers and followings data")
    public void repositoryHasFollowersAndFollowingsData() {
        List<SubscriptionLink> followersLinks = List.of(
            SubscriptionBddObjectFactory.subscriptionLink("f1", SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.TARGET_USER_ID),
            SubscriptionBddObjectFactory.subscriptionLink("f2", SubscriptionBddTestConstants.ANOTHER_TARGET_USER_ID, SubscriptionBddTestConstants.TARGET_USER_ID)
        );
        List<SubscriptionLink> followingsLinks = List.of(
            SubscriptionBddObjectFactory.subscriptionLink("g1", SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.TARGET_USER_ID)
        );

        when(subscriptionRepository.findByFollowingId(SubscriptionBddTestConstants.TARGET_USER_ID)).thenReturn(followersLinks);
        when(subscriptionRepository.findByFollowerId(SubscriptionBddTestConstants.USER_ID)).thenReturn(followingsLinks);
        when(subscriptionRepository.countByFollowingId(SubscriptionBddTestConstants.TARGET_USER_ID)).thenReturn(2);
        when(subscriptionRepository.countByFollowerId(SubscriptionBddTestConstants.USER_ID)).thenReturn(1);

        when(subscriptionMapper.toFollowersListResponse(followersLinks))
            .thenReturn(new com.kolmir.subscription_service.dto.subscription.FollowListResponse(2, List.of(7L, 12L)));
        when(subscriptionMapper.toFollowingsListResponse(followingsLinks))
            .thenReturn(new com.kolmir.subscription_service.dto.subscription.FollowListResponse(1, List.of(11L)));
        when(subscriptionMapper.toFollowCountResponse(2))
            .thenReturn(new com.kolmir.subscription_service.dto.subscription.FollowCountResponse(2));
        when(subscriptionMapper.toFollowCountResponse(1))
            .thenReturn(new com.kolmir.subscription_service.dto.subscription.FollowCountResponse(1));
    }

    @When("user requests followers and followings data")
    public void userRequestsFollowersAndFollowingsData() {
        followersResponse = subscriptionService.getFollowersForUser(SubscriptionBddTestConstants.TARGET_USER_ID);
        followingsResponse = subscriptionService.getFollowingsForUser(SubscriptionBddTestConstants.USER_ID);
        followersCount = subscriptionService.getFollowersCountForUser(SubscriptionBddTestConstants.TARGET_USER_ID);
        followingsCount = subscriptionService.getFollowingsCountForUser(SubscriptionBddTestConstants.USER_ID);
    }

    @Then("followers and followings responses are correct")
    public void followersAndFollowingsResponsesAreCorrect() {
        assertEquals(2, followersResponse.count());
        assertEquals(1, followingsResponse.count());
        assertEquals(2, followersCount.followCount());
        assertEquals(1, followingsCount.followCount());
    }

    @Given("reaction counters for main post are likes {int} and dislikes {int}")
    public void reactionCountersForMainPostAreLikesAndDislikes(Integer likes, Integer dislikes) {
        when(postService.isPostExists(SubscriptionBddTestConstants.POST_ID)).thenReturn(true);
        when(reactionRepository.findByPostId(SubscriptionBddTestConstants.POST_ID))
            .thenReturn(Optional.of(SubscriptionBddObjectFactory.reaction(
                SubscriptionBddTestConstants.REACTION_ID,
                SubscriptionBddTestConstants.POST_ID,
                likes,
                dislikes
            )));
        when(reactionMapper.toResponse(any(Reaction.class))).thenAnswer(invocation ->
            SubscriptionBddObjectFactory.reactionResponse(invocation.getArgument(0))
        );
        when(reactionRepository.save(any(Reaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("the user already has dislike reaction for main post")
    public void theUserAlreadyHasDislikeReactionForMainPost() {
        when(interactionEventService.userHasReaction(SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.POST_ID)).thenReturn(true);
        when(interactionEventService.getReacitonFromUser(SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.POST_ID))
            .thenReturn(SubscriptionBddObjectFactory.likeDislikeResponse(
                SubscriptionBddTestConstants.EVENT_ID,
                Action.DISLIKE,
                SubscriptionBddTestConstants.USER_ID,
                SubscriptionBddTestConstants.POST_ID
            ));
    }

    @Given("the user already has like reaction for main post")
    public void theUserAlreadyHasLikeReactionForMainPost() {
        when(interactionEventService.getReacitonFromUser(SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.POST_ID))
            .thenReturn(SubscriptionBddObjectFactory.likeDislikeResponse(
                SubscriptionBddTestConstants.EVENT_ID,
                Action.LIKE,
                SubscriptionBddTestConstants.USER_ID,
                SubscriptionBddTestConstants.POST_ID
            ));
    }

    @When("the user sets like reaction for main post")
    public void theUserSetsLikeReactionForMainPost() {
        AddReactionRequest request = new AddReactionRequest(Action.LIKE.name());
        when(reactionMapper.toCreateEventRequest(request, SubscriptionBddTestConstants.POST_ID, SubscriptionBddTestConstants.USER_ID))
            .thenReturn(SubscriptionBddObjectFactory.reactionEventRequest(
                Action.LIKE.name(),
                SubscriptionBddTestConstants.USER_ID,
                SubscriptionBddTestConstants.POST_ID
            ));

        singleReactionResponse = reactionService.addReaction(request, SubscriptionBddTestConstants.POST_ID);
    }

    @Then("reaction counters become likes {int} and dislikes {int}")
    public void reactionCountersBecomeLikesAndDislikes(Integer likes, Integer dislikes) {
        assertEquals(SubscriptionBddTestConstants.POST_ID, singleReactionResponse.postId());
        assertEquals(likes, singleReactionResponse.likeCount());
        assertEquals(dislikes, singleReactionResponse.dislikeCount());
    }

    @Then("popularity update is triggered twice for main post")
    public void popularityUpdateIsTriggeredTwiceForMainPost() {
        verify(postService, times(2)).updatePopularity(SubscriptionBddTestConstants.POST_ID);
        verify(interactionEventService).delete(SubscriptionBddTestConstants.EVENT_ID);
    }

    @When("the user deletes reaction for main post")
    public void theUserDeletesReactionForMainPost() {
        singleReactionResponse = reactionService.deleteReaction(SubscriptionBddTestConstants.POST_ID);
    }

    @Given("reaction repository has data only for main post")
    public void reactionRepositoryHasDataOnlyForMainPost() {
        when(reactionRepository.findByPostIdIn(Set.of(SubscriptionBddTestConstants.POST_ID, SubscriptionBddTestConstants.SECOND_POST_ID)))
            .thenReturn(List.of(SubscriptionBddObjectFactory.reaction("r1", SubscriptionBddTestConstants.POST_ID, 4, 1)));
        when(reactionMapper.toResponse(any(Reaction.class))).thenAnswer(invocation ->
            SubscriptionBddObjectFactory.reactionResponse(invocation.getArgument(0))
        );
    }

    @When("requesting reaction counters for main and second post")
    public void requestingReactionCountersForMainAndSecondPost() {
        reactionResponses = reactionService.getReactionsForAllPosts(
            Set.of(SubscriptionBddTestConstants.POST_ID, SubscriptionBddTestConstants.SECOND_POST_ID)
        );
    }

    @Then("responses include second post with zero counters")
    public void responsesIncludeSecondPostWithZeroCounters() {
        boolean hasZeroResponse = reactionResponses.stream().anyMatch(response ->
            response.postId().equals(SubscriptionBddTestConstants.SECOND_POST_ID)
                && response.likeCount().equals(0)
                && response.dislikeCount().equals(0)
        );
        assertTrue(hasZeroResponse);
    }

    @Given("interaction event for like already exists")
    public void interactionEventForLikeAlreadyExists() {
        when(interactionEventRepository.existsByUserIdAndPostId(SubscriptionBddTestConstants.USER_ID, SubscriptionBddTestConstants.POST_ID))
            .thenReturn(true);
        Mockito.doNothing().when(interactionUserExistenceService).validateUserExists(SubscriptionBddTestConstants.USER_ID);
    }

    @When("creating duplicate like interaction event")
    public void creatingDuplicateLikeInteractionEvent() {
        thrown = assertThrows(AlreadyExistsException.class,
            () -> interactionEventServiceImpl.save(
                new CreateInteractionEventRequest(Action.LIKE.name(), SubscriptionBddTestConstants.USER_ID, null, SubscriptionBddTestConstants.POST_ID)
            ));
    }

    @Then("interaction event creation fails with already exists error")
    public void interactionEventCreationFailsWithAlreadyExistsError() {
        assertEquals(AlreadyExistsException.class, thrown.getClass());
    }

    @Given("identity service is unavailable")
    public void identityServiceIsUnavailable() {
        RetryableException retryableException = Mockito.mock(RetryableException.class);
        when(userClient.isUserExistsById(SubscriptionBddTestConstants.USER_ID)).thenThrow(retryableException);
    }

    @When("validating user existence through user existence service")
    public void validatingUserExistenceThroughUserExistenceService() {
        thrown = assertThrows(ExternalServiceException.class,
            () -> userExistenceServiceImpl.validateUserExists(SubscriptionBddTestConstants.USER_ID));
    }

    @Then("external service error is thrown")
    public void externalServiceErrorIsThrown() {
        assertEquals(ExternalServiceException.class, thrown.getClass());
    }

    @Given("feed service returns 404 for popularity update")
    public void feedServiceReturnsForPopularityUpdate() {
        FeignClientException clientException = Mockito.mock(FeignClientException.class);
        when(clientException.status()).thenReturn(404);
        Mockito.doThrow(clientException).when(postClient).updatePopularity(SubscriptionBddTestConstants.POST_ID);
    }

    @When("updating popularity through post service facade")
    public void updatingPopularityThroughPostServiceFacade() {
        thrown = assertThrows(jakarta.ws.rs.NotFoundException.class,
            () -> postServiceImpl.updatePopularity(SubscriptionBddTestConstants.POST_ID));
    }

    @Then("post not found error is thrown")
    public void postNotFoundErrorIsThrown() {
        assertEquals(jakarta.ws.rs.NotFoundException.class, thrown.getClass());
    }
}
