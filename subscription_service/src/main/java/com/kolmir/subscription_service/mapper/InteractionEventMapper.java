package com.kolmir.subscription_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.subscription_service.dto.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.LikeDislikeResponse;
import com.kolmir.subscription_service.dto.SubscriptionResponse;
import com.kolmir.subscription_service.model.InteractionEvent;

@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InteractionEventMapper {
    public InteractionEvent toInteractionEvent(CreateInteractionEventRequest request);
    public LikeDislikeResponse toLikeDislikeResponse(InteractionEvent event);
    public SubscriptionResponse toSubscriptionResponse(InteractionEvent event);
}
