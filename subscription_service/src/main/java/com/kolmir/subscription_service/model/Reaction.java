package com.kolmir.subscription_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "reactions")
public class Reaction {
    @Id
    @EqualsAndHashCode.Exclude
    private String id;
    
    @Indexed(unique = true)
    private Long postId;
    
    private Integer likeCount = 0;
    private Integer dislikeCount = 0;
}
