package com.kolmir.subscription_service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.subscription_service.model.Reaction;


@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    public Optional<Reaction> findByPostId(Long postId);
    public List<Reaction> findByPostIdIn(Collection<Long> postIds);
}
