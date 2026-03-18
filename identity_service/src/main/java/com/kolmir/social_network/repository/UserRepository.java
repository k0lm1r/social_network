package com.kolmir.social_network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.social_network.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
