package com.alex.qnr_project.repository;

import com.alex.qnr_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

    // check if a username exists
    boolean existsByUsername(String username);
}
