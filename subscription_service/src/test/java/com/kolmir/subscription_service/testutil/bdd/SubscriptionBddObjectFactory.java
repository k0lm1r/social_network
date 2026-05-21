package com.kolmir.subscription_service.testutil.bdd;

import java.time.LocalDateTime;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.event.LikeDislikeResponse;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.InteractionEvent;
import com.kolmir.subscription_service.model.Reaction;
import com.kolmir.subscription_service.model.SubscriptionLink;

import lombok.experimental.UtilityClass;


@UtilityClass
public class SubscriptionBddObjectFactory {
    public static Reaction reaction(String id, Long postId, Integer likes, Integer dislikes) {
        return new Reaction(id, postId, likes, dislikes);
    }

    public static ReactionResponse reactionResponse(Reaction reaction) {
        return new ReactionResponse(reaction.getPostId(), reaction.getLikeCount(), reaction.getDislikeCount());
    }

    public static LikeDislikeResponse likeDislikeResponse(String id, Action action, Long userId, Long postId) {
        return new LikeDislikeResponse(id, action, userId, postId);
    }

    public static CreateInteractionEventRequest reactionEventRequest(String action, Long userId, Long postId) {
        return new CreateInteractionEventRequest(action, userId, null, postId);
    }

    public static InteractionEvent interactionEvent(String id, Action action, Long userId, Long targetUserId, Long postId) {
        return new InteractionEvent(id, action, userId, targetUserId, postId, LocalDateTime.now());
    }

    public static SubscriptionLink subscriptionLink(String id, Long followerId, Long followingId) {
        return new SubscriptionLink(id, followerId, followingId);
    }
}
