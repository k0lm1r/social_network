package com.kolmir.feed_service.testutil.integration;

public final class FeedIntegrationTestConstants {
    public static final String POSTGRES_IMAGE = "postgres:17-alpine";
    public static final String REDIS_IMAGE = "redis:7-alpine";
    public static final String FEED_TEST_DB = "feed_test_db";
    public static final String DB_USERNAME = "test";
    public static final String DB_PASSWORD = "test";

    public static final long CURRENT_USER_ID = 101L;
    public static final long FOLLOWING_AUTHOR_ID = 202L;
    public static final long OUTSIDER_AUTHOR_ID = 303L;

    public static final String INTEGRATION_POST_TEXT = "integration post";
    public static final String INTEGRATION_COMMENT_TEXT = "integration comment";
    public static final String FOLLOWING_OLD_TEXT = "following old";
    public static final String FOLLOWING_NEW_TEXT = "following new";
    public static final String OUTSIDER_TEXT = "outsider post";

    public static final double ZERO_POPULARITY = 0.0;
    public static final double MEDIUM_POPULARITY = 2.0;
    public static final double HIGH_POPULARITY = 5.0;

    public static final int REACTION_LIKES = 3;
    public static final int REACTION_DISLIKES = 1;
    public static final int PAGE_INDEX = 0;
    public static final int PAGE_SIZE = 10;
    public static final int EXPECTED_FEED_SIZE = 2;

    private FeedIntegrationTestConstants() {
    }
}
