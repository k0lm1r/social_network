package com.kolmir.subscription_service.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;


@FeignClient(name = "feed-service")
public interface PostClient {
    @GetMapping(FEED_POST_ID_URL)
    public Boolean isPostExists(@PathVariable Long postId);

    @PutMapping(FEED_POPULARITY_URL)
    public void updatePopularity(@PathVariable Long postId);
}
