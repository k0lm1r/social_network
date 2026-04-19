package com.kolmir.subscription_service.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
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
@Document(collection = "interaction_events")
public class InteractionEvent {
    @Id
    @EqualsAndHashCode.Exclude
    private String id;
    
    private Action action;
    private Long userId;
    private Long targetUserId;
    private Long postId;
    private LocalDateTime createdAt;
}
