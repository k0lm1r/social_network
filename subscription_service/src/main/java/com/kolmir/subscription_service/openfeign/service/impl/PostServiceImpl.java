package com.kolmir.subscription_service.openfeign.service.impl;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.POST_NOT_FOUND_MESSAGE;

import org.springframework.stereotype.Service;

import com.kolmir.subscription_service.openfeign.PostClient;
import com.kolmir.subscription_service.openfeign.service.PostService;

import feign.FeignException.FeignClientException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostClient postClient;

    @Override
    public boolean isPostExists(Long postId) {
        return postClient.isPostExists(postId);
    }

    @Override
    public void updatePopularity(Long postId) {
        try {
            postClient.updatePopularity(postId);
        } catch (FeignClientException e) {
            if (e.status() == 404)
                throw new NotFoundException(POST_NOT_FOUND_MESSAGE);
        }
    }
}
