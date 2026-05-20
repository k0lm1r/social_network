package com.kolmir.feed_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class PostUtil {
    public static final String ALL_POSTS_CACHE = "all_posts";
    public static final String ALL_FROM_USER_CACHE = "all_from_user";
    public static final String ALL_FROM_USERS_CACHE = "all_from_users";
    public static final String FEEDS_CACHE = "feeds";
    public static final String POST_CACHE = "post";

    public static final String NOT_FOUND_MESSAGE = "Post with this id was not found";
    public static final String POPULARITY_FIELD_NAME = "popularity"; 
    public static final String CREATED_AT_FIELD = "createdAt"; 
    public static final String POST_MAIN_URL = "/api/posts";
    public static final String POST_ID_URL = "/{postId}";
    public static final String POPULARITY_URL = POST_ID_URL + "/popularity";
    public static final String EXISTING_URL = POST_ID_URL + "/exists";
    public static final String FROM_USER_URL = "/user/{userId}";
    public static final String FEED_URL = FROM_USER_URL + "/feed";
}
