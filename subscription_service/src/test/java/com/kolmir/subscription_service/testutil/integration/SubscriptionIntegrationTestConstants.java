package com.kolmir.subscription_service.testutil.integration;

public final class SubscriptionIntegrationTestConstants {
    public static final String MONGO_IMAGE = "mongo:8.2.7";

    public static final long CURRENT_USER_ID = 10L;
    public static final long TARGET_USER_ID = 20L;
    public static final long MAIN_POST_ID = 1000L;
    public static final long SECOND_POST_ID = 2000L;

    public static final int EXPECTED_REACTIONS_SIZE = 2;

    private SubscriptionIntegrationTestConstants() {
    }
}
