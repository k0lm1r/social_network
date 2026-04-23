package com.kolmir.subscription_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscription_links")
@CompoundIndex(
    name = "unique_follower_following",
    def = "{'followerId': 1, 'followingId': 1}",
    unique = true
)
public class SubscriptionLink {
    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private Long followerId;
    private Long followingId;
}
