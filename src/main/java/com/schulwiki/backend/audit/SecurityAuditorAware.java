package com.schulwiki.backend.audit;

import com.schulwiki.backend.auth.security.userDetails.CurrentUserProvider;
import com.schulwiki.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityAuditorAware implements AuditorAware <UserEntity> {
    private final CurrentUserProvider currentUserProvider;

    @Override
    public Optional<UserEntity> getCurrentAuditor() {
        return Optional.of(currentUserProvider.getCurrent());
    }
}
