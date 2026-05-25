package com.schulwiki.backend.role;

import com.schulwiki.backend.auth.security.userDetails.CurrentUserProvider;
import com.schulwiki.backend.error.exception.ForbiddenException;
import com.schulwiki.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequiredRoleAspect {
    private final CurrentUserProvider currentUserProvider;

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        UserEntity currentUser = currentUserProvider.getCurrent();

        if(currentUser.getRole().getWeight() < requireRole.value().getWeight()) {
            throw new ForbiddenException("User does not have the required permissions");
        }
    }
}
