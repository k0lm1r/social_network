package com.kolmir.subscription_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.subscription_service.dto.AddReactionRequest;
import com.kolmir.subscription_service.dto.CreateInteractionEventRequest;
import com.kolmir.subscription_service.dto.DeleteReactionRequest;
import com.kolmir.subscription_service.dto.ReactionResponse;
import com.kolmir.subscription_service.model.Reaction;


@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReactionMapper {
    public ReactionResponse toResponse(Reaction reaction);
    public Reaction toReaction(AddReactionRequest request);
    public Reaction toReaction(DeleteReactionRequest request);
    public CreateInteractionEventRequest toCreateEventRequest(AddReactionRequest request);
}
