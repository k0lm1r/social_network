package com.kolmir.subscription_service.util;

import lombok.experimental.UtilityClass;


@UtilityClass
public class InteractionEventUtil {
    public static final String EVENT_NOT_FOUND_MESSAGE = "Event with this id was not found";
    public static final String EVENT_ALREADY_EXISTS_MESSAGE = "This user already create this or same event";
    public static final String EVENT_MAIN_URL = "/api/events";
}
