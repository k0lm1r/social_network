package com.kolmir.subscription_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.subscription_service.model.InteractionEvent;


@Repository
public interface InteractionEventRepository extends MongoRepository<InteractionEvent, String> {
    public List<InteractionEvent> findByAction(String action);
}