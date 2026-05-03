package com.kolmir.subscription_service.model;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum Action {
    SUBSCRIBE ("SUBSCRIBE"),
    UNSUBSCRIBE ("UNSUBSCRIBE"),
    LIKE ("LIKE"),
    DISLIKE ("DISLIKE");

    private final String name;

    public String getName() {
        return this.name;
    }
}
