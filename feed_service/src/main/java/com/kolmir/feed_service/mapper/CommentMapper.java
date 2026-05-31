package com.kolmir.feed_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.feed_service.dto.comment.*;
import com.kolmir.feed_service.model.Comment;
import com.kolmir.feed_service.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CommentMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public abstract Comment toComment(CommentCreateRequest request);

    public CommentsCountResponse toCommentsCountResponse(Long count) {
        return new CommentsCountResponse(count);
    }
    
    @Mapping(target = "postId", source = "post.id")
    public abstract CommentResponse toResponse(Comment comment);

    @AfterMapping
    public void setPost(@MappingTarget Comment comment, CommentCreateRequest request) {
        comment.setPost(entityManager.getReference(Post.class, request.postId()));
    }
}
