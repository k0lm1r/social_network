package com.kolmir.subscription_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.exception.AlreadyExistsException;
import com.kolmir.subscription_service.exception.NotFoundException;
import com.kolmir.subscription_service.mapper.InteractionEventMapper;
import com.kolmir.subscription_service.mapper.SubscriptionLinkMapper;
import com.kolmir.subscription_service.model.Action;
import com.kolmir.subscription_service.model.SubscriptionLink;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.repository.SubscriptionLinkRepository;
import com.kolmir.security.provider.CurrentUserProvider;
import com.kolmir.subscription_service.service.InteractionEventService;
import com.kolmir.subscription_service.service.SubscriptionLinkService;
import static com.kolmir.subscription_service.util.SubscriptionLinkUtil.*;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionLinkServiceImpl implements SubscriptionLinkService {
    private final SubscriptionLinkRepository repository;
    private final SubscriptionLinkMapper subscriptionLinkMapper;
    private final InteractionEventMapper eventMapper;
    private final InteractionEventService interactionEventService;
    private final UserExistenceService userExistenceService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public SubscriptionLinkResponse follow(Long followingId) {
        userExistenceService.validateUserExists(followingId);

        if (followingId == currentUserProvider.getCurrentUserId())
            throw new IllegalArgumentException(FOLLOW_YOURSELF_MESSAGE);
        if (isUserFollower(currentUserProvider.getCurrentUserId(), followingId))
            throw new AlreadyExistsException(ALREADY_FOLLOWER_MESSAGE);

        SubscriptionLink subscriptionLink = repository.save(
            subscriptionLinkMapper.toSubscriptionLink(
                followingId, 
                currentUserProvider.getCurrentUserId()
            )
        );

        interactionEventService.save(
            eventMapper.toInteractionEventRequest(
                subscriptionLink, 
                Action.SUBSCRIBE.getName()
            )
        );

        return subscriptionLinkMapper.toSubscriptionLinkResponse(subscriptionLink);
    }

    @Override
    public void unfollow(Long followingId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        SubscriptionLink link = getByFollowerAndFollowingId(currentUserId, followingId);
        repository.delete(link);
        interactionEventService.save(
            eventMapper.toInteractionEventRequest(
                link, 
                Action.UNSUBSCRIBE.getName()
            )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountResponse getFollowersCountForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return subscriptionLinkMapper.toFollowCountResponse (
            repository.countByFollowingId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowListResponse getFollowersForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return subscriptionLinkMapper.toFollowersListResponse(
            repository.findByFollowingId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountResponse getFollowingsCountForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return subscriptionLinkMapper.toFollowCountResponse(
            repository.countByFollowerId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FollowListResponse getFollowingsForUser(Long userId) {
        userExistenceService.validateUserExists(userId);
        return subscriptionLinkMapper.toFollowingsListResponse(
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
