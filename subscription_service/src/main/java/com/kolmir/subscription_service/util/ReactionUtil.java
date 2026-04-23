package com.kolmir.subscription_service.util;

import lombok.experimental.UtilityClass;
import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;


@UtilityClass
public class ReactionUtil {
    public static final String POST_MAIN_URL = "/api/posts";
    public static final String REACTIONS_URL = "/reactions";
    public static final String REACTIONS_FOR_POST_URL = ID_URL + REACTIONS_URL;
}
