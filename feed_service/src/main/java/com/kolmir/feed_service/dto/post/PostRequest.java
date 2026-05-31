package com.kolmir.feed_service.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record PostRequest (
    @NotBlank(message = "{notblank}")
    @Size(max = 1000, message = "{size}")
    String text
) {}
