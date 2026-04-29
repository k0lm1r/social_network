package com.kolmir.subscription_service.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.model.SubscriptionLink;
import com.kolmir.subscription_service.security.SecurityUtils;


@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SubscriptionLinkMapper {
    public SubscriptionLinkResponse toSubscriptionLinkResponse(SubscriptionLink link);
    
    public default FollowCountResponse toFollowCountResponse(int count) {
        return new FollowCountResponse(count);
    }

    public default FollowListResponse toFollowersListResponse(Collection<SubscriptionLink> links) {
        return new FollowListResponse(
            toFollowCountResponse(links.size()), 
            links.stream()
                .map(SubscriptionLink::getFollowerId)
                .toList()
        );
    }

    public default FollowListResponse toFollowingsListResponse(Collection<SubscriptionLink> links) {
        return new FollowListResponse(
            toFollowCountResponse(links.size()), 
            links.stream()
                .map(SubscriptionLink::getFollowingId)
                .toList()
        );
    }

    public default SubscriptionLink toSubscriptionLink(Long followingId) {
        var link = new SubscriptionLink();
        link.setFollowerId(SecurityUtils.getCurrentUser().id());
        link.setFollowingId(followingId);
        return link;
    }
}
