package com.alex.qnr_project.repository;

import com.alex.qnr_project.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    // check if a token exists in the blacklist
    Optional<BlacklistedToken> findByToken(String token);

    boolean existsByToken(String token);
}
