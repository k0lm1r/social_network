package com.kolmir.subscription_service.openfeign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;


@FeignClient(name = "identity-service")
public interface UserClient {
    @GetMapping(USER_ID_URL)
    Map<Object, Object> getUserById(@PathVariable Long userId);
}
