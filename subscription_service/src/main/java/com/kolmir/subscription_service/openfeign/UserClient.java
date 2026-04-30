package com.kolmir.subscription_service.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;


@FeignClient(name = "identity-service")
public interface UserClient {
    @GetMapping(IS_USER_EXISTS_URL)
    public Boolean isUserExistsById(@PathVariable Long userId);
}
