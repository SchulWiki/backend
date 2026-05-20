package com.schulwiki.backend.auth.security.userDetails;

import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.error.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public UserEntity getCurrent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserEntity();
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }
}
