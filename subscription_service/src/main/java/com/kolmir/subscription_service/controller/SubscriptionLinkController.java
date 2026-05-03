package com.kolmir.subscription_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.subscription_service.dto.subscription.FollowCountResponse;
import com.kolmir.subscription_service.dto.subscription.FollowListResponse;
import com.kolmir.subscription_service.dto.subscription.SubscriptionLinkResponse;
import com.kolmir.subscription_service.service.SubscriptionLinkService;

import lombok.RequiredArgsConstructor;

import static com.kolmir.subscription_service.util.SubscriptionLinkUtil.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping(SUBSCRIPTION_BASE_URL)
public class SubscriptionLinkController {
    private final SubscriptionLinkService service;

    @GetMapping(FOLLOWERS_LIST_URL)
    public ResponseEntity<FollowListResponse> getFollowersList(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowersForUser(userId));
    }

    @GetMapping(FOLLOWINGS_LIST_URL)
    public ResponseEntity<FollowListResponse> getFollowingsList(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowingsForUser(userId));
    }

    @GetMapping(FOLLOWERS_COUNT_URL)
    public ResponseEntity<FollowCountResponse> getFollowersCount(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowersCountForUser(userId));
    }

    @GetMapping(FOLLOWINGS_COUNT_URL)
    public ResponseEntity<FollowCountResponse> getFollowingsCount(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFollowingsCountForUser(userId));
    }

    @GetMapping(IS_FOLLOWER_ID_URL)
    public ResponseEntity<Boolean> isUserFollower(@PathVariable Long followerId, @PathVariable Long followingId) {
        return ResponseEntity.ok(service.isUserFollower(followerId, followingId));
    }
    
    @PostMapping(FOLLOW_URL)
    public ResponseEntity<SubscriptionLinkResponse> follow(@PathVariable Long targetUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.follow(targetUserId));
    }
    

    @DeleteMapping(UNFOLLOW_URL)
    public ResponseEntity<Void> unfollow(@PathVariable Long targetUserId) {
        service.unfollow(targetUserId);
        return ResponseEntity.noContent().build();
    }
}
