package com.kolmir.subscription_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.mapper.SubscriptionLinkMapper;
import com.kolmir.subscription_service.model.SubscriptionLink;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.repository.SubscriptionLinkRepository;
import com.kolmir.subscription_service.security.SecurityUtils;
import com.kolmir.subscription_service.service.SubscriptionLinkService;
import static com.kolmir.subscription_service.util.SubscriptionLinkUtil.*;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionLinkServiceImpl implements SubscriptionLinkService {
    private final SubscriptionLinkRepository repository;
    private final SubscriptionLinkMapper mapper;
    private final UserExistenceService userExistenceService;

    @Override
    public SubscriptionLinkResponse follow(Long followingId) {
        userExistenceService.validateUserExists(followingId);
        return mapper.toSubscriptionLinkResponse(
            repository.save(
                mapper.toSubscriptionLink(followingId)
            )
        );
    }

    @Override
    public void unfollow(Long followingId) {
        Long currentUserId = SecurityUtils.getCurrentUser().id();
        SubscriptionLink link = getByFollowerAndFollowingId(currentUserId, followingId);
        repository.delete(link);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountResponse getFollowersCountForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return mapper.toFollowCountResponse (
            repository.countByFollowingId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowListResponse getFollowersForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return mapper.toFollowersListResponse(
            repository.findByFollowingId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountResponse getFollowingsCountForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return mapper.toFollowCountResponse(
            repository.countByFollowerId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowListResponse getFollowingsForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return mapper.toFollowingsListResponse(
            repository.findByFollowerId(userId)
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Boolean isUserFollower(Long userId, Long followingId) {
        return repository.existsByFollowerIdAndFollowingId(userId, followingId);
    }

    private SubscriptionLink getByFollowerAndFollowingId(Long followerId, Long followingId) {
        SubscriptionLink link = repository.findByFollowerIdAndFollowingId(followerId, followingId).orElseThrow(
            () -> new NotFoundException(NOT_FOUND_MESSAGE)
        );
        return link;
    } 
}
