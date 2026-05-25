package com.schulwiki.backend.auth.service;

import com.schulwiki.backend.auth.dto.LoginRequest;
import com.schulwiki.backend.auth.dto.AuthResponse;
import com.schulwiki.backend.auth.dto.RegisterRequest;
import com.schulwiki.backend.auth.dto.ValidationCredentialsRequest;
import com.schulwiki.backend.auth.security.jwt.JwtService;
import com.schulwiki.backend.auth.security.refreshToken.dto.RefreshTokenRequest;
import com.schulwiki.backend.auth.security.refreshToken.entity.RefreshToken;
import com.schulwiki.backend.auth.security.refreshToken.service.RefreshTokenService;
import com.schulwiki.backend.auth.security.userDetails.CurrentUserProvider;
import com.schulwiki.backend.auth.security.userDetails.CustomUserDetails;
import com.schulwiki.backend.user.dto.UserResponse;
import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.user.mapper.UserMapper;
import com.schulwiki.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.schulwiki.backend.error.exception.ConflictException;
import com.schulwiki.backend.error.exception.UnauthorizedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterRequest request) {
        log.info("Registering new user with username: {}", request.getValidationCredentialsRequest().getUsername());

        if (userService.existsByUsername(request.getValidationCredentialsRequest().getUsername())) {
            throw new ConflictException("Username already exists");
        }
        UserEntity user = userMapper.mapToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getValidationCredentialsRequest().getPassword()));

        userService.save(user);
        log.info("User {} registered successfully", request.getValidationCredentialsRequest().getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, String fingerprint) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = customUserDetails.getUserEntity();

        String token = jwtService.generateToken(user.getId(), fingerprint);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), fingerprint);


        log.info("User {} authenticated successfully", loginRequest.getUsername());
        return new AuthResponse(refreshToken.getToken(), token);
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest, String fingerprint) {
        RefreshToken refreshToken = refreshTokenService.validateToken(refreshTokenRequest.getToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token expired or invalid"));

        log.info("Attempting to refresh token for user ID: {}", refreshToken.getUser().getId());

        if (!refreshToken.getFingerprint().equals(fingerprint)) {
            refreshTokenService.revokeSessionIsolated(refreshToken.getToken());

            log.warn("FINGERPRINT MISMATCH! Potential token theft for user ID: {}. Session revoked.", refreshToken.getUser().getId());
            throw new UnauthorizedException("Invalid device fingerprint. Session revoked.");
        }

        UserEntity user = userService.findById(refreshToken.getUser().getId());

        String newAccessToken = jwtService.generateToken(user.getId(), fingerprint);

        // Refresh token rotation
        refreshTokenService.revokeSessionIsolated(refreshTokenRequest.getToken());
        refreshToken = refreshTokenService.createRefreshToken(user.getId(), fingerprint);

        log.info("Token refreshed successfully for user: {}", user.getUsername());

        return new AuthResponse(refreshToken.getToken(), newAccessToken);
    }

    @Transactional
    public void logout(RefreshTokenRequest refreshRequest) {
        log.info("Processing logout for current context");
        SecurityContextHolder.clearContext();
        if (refreshRequest != null && refreshRequest.getToken() != null) {
            refreshTokenService.revokeSessionIsolated(refreshRequest.getToken());
        }
    }

    @Transactional
    public UserResponse me() {
        log.info("Fetching current user details");
        UserEntity currentUser = currentUserProvider.getCurrent();
        UserEntity user = userService.findById(currentUser.getId());
        return userMapper.mapToResponse(user);
    }

    public void validateCredentials(@Valid ValidationCredentialsRequest request) {
        if(userService.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ConflictException("Password and Confirm Password do not match");
        }
    }
}
