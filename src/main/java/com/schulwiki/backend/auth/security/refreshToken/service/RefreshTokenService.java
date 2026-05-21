package com.schulwiki.backend.auth.security.refreshToken.service;

import com.schulwiki.backend.auth.security.refreshToken.entity.RefreshToken;
import com.schulwiki.backend.auth.security.refreshToken.repository.RefreshTokenRepository;
import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.schulwiki.backend.error.exception.NotFoundException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, @Value("${jwt.refreshExpiration}") long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshExpirationMs = refreshTokenExpirationMs;
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId, String fingerprint) {
        log.info("Creating new refresh token for user ID: {}", userId);
        refreshTokenRepository.deleteByFingerprintAndUserId(fingerprint, userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshExpirationMs / 1000));
        refreshToken.setFingerprint(fingerprint);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> validateToken(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOpt.isEmpty()) {
            log.warn("Attempted to validate non-existent refresh token");
            return Optional.empty();
        }

        RefreshToken token = tokenOpt.get();
        if (token.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Refresh token expired for user ID: {}", token.getUser().getId());
            return Optional.empty();
        }

        return tokenOpt;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeSessionIsolated(String refreshToken) {
        log.info("Isolated transaction: Deleting specific refresh token");
        refreshTokenRepository.deleteByToken(refreshToken);
        refreshTokenRepository.flush();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllSessionTracesIsolated(Long userId) {
        log.warn("Isolated transaction: Revoking ALL refresh tokens for user ID: {} due to security breach / fingerprint mismatch", userId);
        refreshTokenRepository.deleteAllByUserId(userId);
        refreshTokenRepository.flush();
    }
}
