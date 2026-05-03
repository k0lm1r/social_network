package com.kolmir.subscription_service.dto.event;

public sealed interface InteractionEventResponse permits LikeDislikeResponse, SubscriptionResponse {
}
