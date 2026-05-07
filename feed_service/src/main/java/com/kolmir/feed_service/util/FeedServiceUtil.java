package com.kolmir.feed_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class FeedServiceUtil {
    public static final String GET_REACTIONS_URL = "/api/reactions/{postId}";
    public static final String GET_FOLLOWINGS_URL = "/api/subscriptions/{userId}/followings";
}
