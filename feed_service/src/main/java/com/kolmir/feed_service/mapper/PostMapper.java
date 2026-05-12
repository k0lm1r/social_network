package com.kolmir.feed_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.kolmir.feed_service.dto.post.PostRequest;
import com.kolmir.feed_service.dto.post.PostResponse;
import com.kolmir.feed_service.model.Post;


@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {
    public abstract Post toPost(PostRequest request);
    public abstract PostResponse toResponse(Post post);
}
