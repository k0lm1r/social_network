package com.kolmir.feed_service.testutil.integration;

import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.DB_PASSWORD;
import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.DB_USERNAME;
import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.FEED_TEST_DB;
import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.POSTGRES_IMAGE;
import static com.kolmir.feed_service.testutil.integration.FeedIntegrationTestConstants.REDIS_IMAGE;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("resource")
@Testcontainers
public abstract class FeedTestContainers {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withDatabaseName(FEED_TEST_DB)
        .withUsername(DB_USERNAME)
        .withPassword(DB_PASSWORD);

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(REDIS_IMAGE)
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);
    }
}
