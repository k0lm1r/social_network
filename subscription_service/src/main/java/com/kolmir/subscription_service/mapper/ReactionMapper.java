package com.kolmir.subscription_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.subscription_service.dto.event.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.reaction.AddReactionRequest;
import com.kolmir.subscription_service.dto.reaction.ReactionResponse;
import com.kolmir.subscription_service.model.Reaction;


@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReactionMapper {
    public ReactionResponse toResponse(Reaction reaction);
    public Reaction toReaction(AddReactionRequest request);

    public default CreateInteractionEventRequest toCreateEventRequest(AddReactionRequest request, Long postId, Long userId) {
        return new CreateInteractionEventRequest(
            request.action(), 
            userId, 
            null,
            postId
        );
    }
}
