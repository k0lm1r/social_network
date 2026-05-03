package com.kolmir.subscription_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.subscription_service.model.SubscriptionLink;


@Repository
public interface SubscriptionLinkRepository extends MongoRepository<SubscriptionLink, String> {

    public List<SubscriptionLink> findByFollowerId(Long followerId);
    public List<SubscriptionLink> findByFollowingId(Long followingId);
    public Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    public Optional<SubscriptionLink> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    public int countByFollowingId(Long followingId);
    public int countByFollowerId(Long followerId);
}
