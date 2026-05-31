package com.kolmir.feed_service.dto.comment;

import java.time.LocalDateTime;


public record CommentResponse (
    Long id,
    Long authorId,
    Long postId,
    String text,
    LocalDateTime createdAt
) {}
