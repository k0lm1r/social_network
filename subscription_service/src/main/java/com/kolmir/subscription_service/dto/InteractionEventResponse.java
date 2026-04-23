package com.kolmir.subscription_service.dto;

public sealed interface InteractionEventResponse permits LikeDislikeResponse, SubscriptionResponse {
}
