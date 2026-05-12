package com.kolmir.subscription_service.util;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.USER_ID_URL;

import lombok.experimental.UtilityClass;


@UtilityClass
public class SubscriptionLinkUtil {
    public final static String NOT_FOUND_MESSAGE = "Current user don't follow this account"; 
    public final static String FOLLOW_YOURSELF_MESSAGE = "You can't follow yourself";   
    public final static String SUBSCRIPTION_MAIN_URL = "/api/subscriptions";
    public final static String FOLLOWERS_LIST_URL = USER_ID_URL + "/followers";
    public final static String FOLLOWINGS_LIST_URL = USER_ID_URL + "/followings";
    public final static String COUNT_URL = "/count";
    public final static String FOLLOWERS_COUNT_URL = FOLLOWERS_LIST_URL + COUNT_URL;
    public final static String FOLLOWINGS_COUNT_URL = FOLLOWINGS_LIST_URL + COUNT_URL;
    public final static String TARGET_USER_ID_URL = "/{targetUserId}";
    public final static String FOLLOW_URL = TARGET_USER_ID_URL + "/follow";
    public final static String UNFOLLOW_URL = TARGET_USER_ID_URL + "/unfollow";
    public final static String IS_FOLLOWER_ID_URL = "/{followerId}/following/{followingId}";
}
