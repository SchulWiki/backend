package com.schulwiki.backend.auth.security.refreshToken.repository;

import com.schulwiki.backend.auth.security.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserId(Long id);

    void deleteByFingerprintAndUserId(String fingerprint, Long userId);
}
