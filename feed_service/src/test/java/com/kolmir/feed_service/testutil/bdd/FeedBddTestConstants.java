package com.kolmir.feed_service.testutil.bdd;

import lombok.experimental.UtilityClass;


@UtilityClass
public class FeedBddTestConstants {
    public static final Long USER_ID = 1L;
    public static final Long OWNER_ID = 77L;
    public static final Long ANOTHER_USER_ID = 88L;

    public static final Long FIRST_AUTHOR_ID = 10L;
    public static final Long SECOND_AUTHOR_ID = 20L;

    public static final Long POST_ID = 42L;
    public static final Long MISSING_POST_ID = 500L;
    public static final Long COMMENT_ID = 21L;
    public static final Long MISSING_COMMENT_ID = 901L;

    public static final String POST_TEXT = "Hello BDD";
    public static final String UPDATED_POST_TEXT = "Updated text";

    public static final Integer LIKES = 3;
    public static final Integer DISLIKES = 1;
    public static final Long COMMENTS = 2L;
    public static final Double POPULARITY = 7.5;

    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
}
