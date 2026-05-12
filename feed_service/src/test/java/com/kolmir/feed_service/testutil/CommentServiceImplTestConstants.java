package com.kolmir.feed_service.testutil;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentServiceImplTestConstants {
    public static final Long POST_ID = 100L;
    public static final Long USER_ID = 77L;
    public static final Long COMMENT_ID = 10L;
    public static final Long MISSING_COMMENT_ID = 99L;
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
    public static final Integer COMMENTS_COUNT = 4;

    public static final String COMMENT_TEXT = "new comment";
}
