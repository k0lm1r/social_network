package com.kolmir.feed_service.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.kolmir.feed_service.openfeign.dto.FollowListResponse;
import com.kolmir.feed_service.openfeign.dto.ReactionResponse;

import static com.kolmir.feed_service.util.FeedServiceUtil.*;


@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping(GET_FOLLOWINGS_URL)
    public FollowListResponse getAllFollowingsForUser(@PathVariable Long userId);

    @GetMapping(GET_REACTIONS_URL)
    public ReactionResponse getReactionsForPosts(@PathVariable Long postIds);
}
