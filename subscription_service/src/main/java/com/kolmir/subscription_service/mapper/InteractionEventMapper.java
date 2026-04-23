package com.kolmir.subscription_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.subscription_service.dto.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.InteractionEventResponse;
import com.kolmir.subscription_service.dto.LikeDislikeResponse;
import com.kolmir.subscription_service.dto.SubscriptionResponse;
import com.kolmir.subscription_service.model.InteractionEvent;

@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InteractionEventMapper {
    public InteractionEvent toInteractionEvent(CreateInteractionEventRequest request);
    public List<InteractionEventResponse> toResponses(List<InteractionEvent> events);

    public default InteractionEventResponse toResponse(InteractionEvent event) {
        return switch (event.getAction()) {
            case SUBSCRIBE, UNSUBSCRIBE -> new SubscriptionResponse(event.getId(), event.getAction(), event.getUserId(), event.getTargetUserId());
            case LIKE, DISLIKE -> new LikeDislikeResponse(event.getId(), event.getAction(), event.getUserId(), event.getPostId());
        };
    }
}
