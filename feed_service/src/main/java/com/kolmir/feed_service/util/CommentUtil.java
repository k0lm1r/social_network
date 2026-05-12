package com.kolmir.feed_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class CommentUtil {
    public static final String NOT_FOUND_MESSAGE = "Comment with this id was not found";
    public static final String COMMENT_MAIN_URL = "/api/comments";
    public static final String COMMENT_ID_URL = "/{commentId}";
    public static final String COMMENTS_FOR_POST = "/post/{postId}";
    public static final String COMMENTS_BY_USER = COMMENTS_FOR_POST + "/{userId}";
    public static final String COMMENTS_COUNT_FOR_POST = COMMENTS_FOR_POST + "/count";
}
