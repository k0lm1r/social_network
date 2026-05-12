package com.kolmir.feed_service.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest (
    @Positive(message = "{positive}")
    Long postId,

    @NotBlank(message = "{notblank}")
    @Size(max = 1000, message = "{size}")
    String text
) {}
