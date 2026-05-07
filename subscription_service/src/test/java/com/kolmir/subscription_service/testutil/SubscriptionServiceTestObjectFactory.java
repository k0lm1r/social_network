package com.kolmir.subscription_service.testutil;

import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestConstants.*;

import java.time.LocalDateTime;
import java.util.List;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.event.LikeDislikeResponse;
import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.InteractionEvent;
import com.kolmir.subscription_service.model.Reaction;
import com.kolmir.subscription_service.model.SubscriptionLink;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SubscriptionServiceTestObjectFactory {
    public static AddReactionRequest addLikeReactionRequest() {
        return new AddReactionRequest(Action.LIKE.getName());
    }

    public static CreateInteractionEventRequest createLikeEventRequest() {
        return new CreateInteractionEventRequest(Action.LIKE.getName(), USER_ID, null, POST_ID);
    }

    public static CreateInteractionEventRequest createSubscribeEventRequest() {
        return new CreateInteractionEventRequest(Action.SUBSCRIBE.getName(), USER_ID, TARGET_USER_ID, null);
    }

    public static InteractionEvent likeInteractionEvent() {
        return new InteractionEvent(EVENT_ID, Action.LIKE, USER_ID, null, POST_ID, LocalDateTime.now());
    }

    public static LikeDislikeResponse likeDislikeResponse() {
        return new LikeDislikeResponse(EVENT_ID, Action.LIKE, USER_ID, POST_ID);
    }

    public static LikeDislikeResponse previousDislikeResponse() {
        return new LikeDislikeResponse(EVENT_ID, Action.DISLIKE, USER_ID, POST_ID);
    }

    public static Reaction reaction(Long postId, int likeCount, int dislikeCount) {
        return new Reaction(REACTION_ID, postId, likeCount, dislikeCount);
    }

    public static Reaction existingReaction() {
        return reaction(POST_ID, 2, 1);
    }

    public static Reaction emptyReaction() {
        return reaction(POST_ID, 0, 0);
    }

    public static Reaction firstPostReaction() {
        return reaction(FIRST_POST_ID, 4, 2);
    }

    public static ReactionResponse reactionResponse(Long postId, int likeCount, int dislikeCount) {
        return new ReactionResponse(postId, likeCount, dislikeCount);
    }

    public static SubscriptionLink unsavedSubscriptionLink() {
        return new SubscriptionLink(null, CURRENT_USER_ID, FOLLOWING_ID);
    }

    public static SubscriptionLink savedSubscriptionLink() {
        return new SubscriptionLink(LINK_ID, CURRENT_USER_ID, FOLLOWING_ID);
    }

    public static SubscriptionLinkResponse subscriptionLinkResponse() {
        return new SubscriptionLinkResponse(CURRENT_USER_ID, FOLLOWING_ID);
    }

    public static List<SubscriptionLink> followerLinks() {
        return List.of(
            new SubscriptionLink(FIRST_LINK_ID, FIRST_FOLLOWER_ID, FOLLOWING_ID),
            new SubscriptionLink(SECOND_LINK_ID, SECOND_FOLLOWER_ID, FOLLOWING_ID)
        );
    }

    public static List<SubscriptionLink> followingLinks() {
        return List.of(
            new SubscriptionLink(FIRST_LINK_ID, CURRENT_USER_ID, FIRST_FOLLOWING_ID),
            new SubscriptionLink(SECOND_LINK_ID, CURRENT_USER_ID, SECOND_FOLLOWING_ID)
        );
    }

    public static FollowCountResponse followCountResponse(int count) {
        return new FollowCountResponse(count);
    }

    public static FollowListResponse followersListResponse() {
        return new FollowListResponse(
            2,
            List.of(FIRST_FOLLOWER_ID, SECOND_FOLLOWER_ID)
        );
    }

    public static FollowListResponse followingsListResponse() {
        return new FollowListResponse(
            2,
            List.of(FIRST_FOLLOWING_ID, SECOND_FOLLOWING_ID)
        );
    }
}
