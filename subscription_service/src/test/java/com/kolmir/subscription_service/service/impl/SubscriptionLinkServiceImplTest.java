package com.kolmir.subscription_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestConstants.*;
import static com.kolmir.subscription_service.testutil.SubscriptionServiceTestObjectFactory.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.security.provider.CurrentUserProvider;
import com.kolmir.subscription_service.exception.NotFoundException;
import com.kolmir.subscription_service.mapper.SubscriptionLinkMapper;
import com.kolmir.subscription_service.openfeign.service.UserExistenceService;
import com.kolmir.subscription_service.repository.SubscriptionLinkRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionLinkServiceImplTest {
    @Mock
    private SubscriptionLinkRepository repository;

    @Mock
    private SubscriptionLinkMapper mapper;

    @Mock
    private UserExistenceService userExistenceService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private SubscriptionLinkServiceImpl service;

    @Test
    void follow_validatesTargetUserAndSavesLinkForCurrentUser() {
        var link = unsavedSubscriptionLink();
        var saved = savedSubscriptionLink();
        var response = subscriptionLinkResponse();

        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(mapper.toSubscriptionLink(FOLLOWING_ID, CURRENT_USER_ID)).thenReturn(link);
        when(repository.save(link)).thenReturn(saved);
        when(mapper.toSubscriptionLinkResponse(saved)).thenReturn(response);

        assertThat(service.follow(FOLLOWING_ID)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(FOLLOWING_ID);
    }

    @Test
    void unfollow_deletesExistingLink() {
        var link = savedSubscriptionLink();

        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(repository.findByFollowerIdAndFollowingId(CURRENT_USER_ID, FOLLOWING_ID))
            .thenReturn(Optional.of(link));

        service.unfollow(FOLLOWING_ID);

        verify(repository).delete(link);
    }

    @Test
    void unfollow_throwsWhenCurrentUserDoesNotFollowTarget() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(repository.findByFollowerIdAndFollowingId(CURRENT_USER_ID, FOLLOWING_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.unfollow(FOLLOWING_ID))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFollowersCountForUser_validatesUserAndReturnsCount() {
        var response = followCountResponse(FOLLOWERS_COUNT);

        when(repository.countByFollowingId(FOLLOWING_ID)).thenReturn(FOLLOWERS_COUNT);
        when(mapper.toFollowCountResponse(FOLLOWERS_COUNT)).thenReturn(response);

        assertThat(service.getFollowersCountForUser(FOLLOWING_ID)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(FOLLOWING_ID);
    }

    @Test
    void getFollowersForUser_validatesUserAndReturnsFollowers() {
        var links = followerLinks();
        var response = followersListResponse();

        when(repository.findByFollowingId(FOLLOWING_ID)).thenReturn(links);
        when(mapper.toFollowersListResponse(links)).thenReturn(response);

        assertThat(service.getFollowersForUser(FOLLOWING_ID)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(FOLLOWING_ID);
    }

    @Test
    void getFollowingsForUser_validatesUserAndReturnsFollowings() {
        var links = followingLinks();
        var response = followingsListResponse();

        when(repository.findByFollowerId(CURRENT_USER_ID)).thenReturn(links);
        when(mapper.toFollowingsListResponse(links)).thenReturn(response);

        assertThat(service.getFollowingsForUser(CURRENT_USER_ID)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(CURRENT_USER_ID);
    }

    @Test
    void getFollowingsCountForUser_validatesUserAndReturnsCount() {
        var response = followCountResponse(FOLLOWINGS_COUNT);

        when(repository.countByFollowerId(CURRENT_USER_ID)).thenReturn(FOLLOWINGS_COUNT);
        when(mapper.toFollowCountResponse(FOLLOWINGS_COUNT)).thenReturn(response);

        assertThat(service.getFollowingsCountForUser(CURRENT_USER_ID)).isEqualTo(response);
        verify(userExistenceService).validateUserExists(CURRENT_USER_ID);
    }

    @Test
    void isUserFollower_delegatesToRepository() {
        when(repository.existsByFollowerIdAndFollowingId(CURRENT_USER_ID, FOLLOWING_ID)).thenReturn(true);

        assertThat(service.isUserFollower(CURRENT_USER_ID, FOLLOWING_ID)).isTrue();
    }
}
