package com.kolmir.subscription_service.testutil.integration;

import static com.kolmir.subscription_service.testutil.integration.SubscriptionIntegrationTestConstants.MONGO_IMAGE;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class SubscriptionTestContainers {
    @Container
    private static final MongoDBContainer MONGO = new MongoDBContainer(MONGO_IMAGE);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", MONGO::getReplicaSetUrl);
    }
}
