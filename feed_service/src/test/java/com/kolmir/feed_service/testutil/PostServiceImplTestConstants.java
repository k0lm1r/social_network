package com.kolmir.feed_service.testutil;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostServiceImplTestConstants {
    public static final Long CURRENT_USER_ID = 77L;
    public static final Long AUTHOR_ID = 12L;
    public static final Long USER_ID = 15L;
    public static final Long FOLLOWING_ID = 10L;
    public static final Long POST_ID = 5L;
    public static final Long FEED_POST_ID = 9L;
    public static final Long MISSING_POST_ID = 100L;
    public static final Long CREATED_POST_ID = 10L;
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
    public static final Integer LIKE_COUNT = 4;
    public static final Integer DISLIKE_COUNT = 2;
    public static final Integer COMMENTS_COUNT = 3;

    public static final Double DEFAULT_POPULARITY = 0.0;
    public static final Double FEED_POPULARITY = 1.0;
    public static final Double EXPECTED_POPULARITY = 10.0;

    public static final String NEW_POST_TEXT = "new post";
    public static final String FEED_POST_TEXT = "p";
    public static final String POST_TEXT = "text";
    public static final String UPDATED_POST_TEXT = "updated text";
}
