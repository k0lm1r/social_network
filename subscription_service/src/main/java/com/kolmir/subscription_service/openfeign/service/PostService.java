package com.kolmir.subscription_service.openfeign.service;


public interface PostService {
    public boolean isPostExists(Long postId);
    public void updatePopularity(Long postId);
}
