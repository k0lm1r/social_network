package com.kolmir.feed_service.dto.post;

import java.time.LocalDateTime;


public record PostResponse(
    Long id,
    Long authorId,
    String text,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
