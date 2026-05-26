package com.schulwiki.backend.user.service;

import com.schulwiki.backend.auth.security.userDetails.CurrentUserProvider;
import com.schulwiki.backend.error.exception.BadRequestException;
import com.schulwiki.backend.error.exception.ForbiddenException;
import com.schulwiki.backend.error.exception.NotFoundException;
import com.schulwiki.backend.role.Role;
import com.schulwiki.backend.user.dto.*;
import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.user.filter.UserFilter;
import com.schulwiki.backend.user.mapper.UserMapper;
import com.schulwiki.backend.user.repository.UserRepository;
import com.schulwiki.backend.user.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public UserResponse getUserById(Long id) {
        return userMapper.mapToResponse(findById(id));
    }

    public Page<UserResponse> getUsers(UserFilter filter, Pageable pageable) {
        return userRepository.findAll(UserSpecification.withFilter(filter), pageable).map(userMapper::mapToResponse);
    }

    public void updateUserProfile(Long id, UserProfileUpdateRequest request) {
        verifyUserAccess(id);

        if(request.getFirstName() == null && request.getLastName() == null) {
            throw new BadRequestException("At least one of firstName or lastName must be provided");
        }
        UserEntity user = findById(id);

        boolean updated = false;
        if(request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(request.getFirstName());
            updated = true;
        }
        if(request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
            user.setLastName(request.getLastName());
            updated = true;
        }

        if(!updated) {
            throw new BadRequestException("No changes detected in firstName or lastName");
        }

        userRepository.save(user);
    }

    public void updateUserIdentity(Long id, UserIdentityUpdateRequest request) {
        verifyUserAccess(id);

        if(request.getUsername() == null && request.getEmail() == null) {
            throw new BadRequestException("At least one of username or email must be provided");
        }
        UserEntity user = findById(id);

        boolean updated = false;
        if(request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if(existsByUsername(request.getUsername())) {
                throw new BadRequestException("Username already exists");
            }
            user.setUsername(request.getUsername());
            updated = true;
        }
        if(request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if(existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already used");
            }
            user.setEmail(request.getEmail());
            updated = true;
        }

        if(!updated) {
            throw new BadRequestException("No changes detected in username or email");
        }

        userRepository.save(user);
    }

    public void updateUserPassword(Long id, UserPasswordUpdateRequest request) {
        verifyUserAccess(id);

        UserEntity user = findById(id);

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from old password");
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void updateUserRole(Long id, UserRoleUpdateRequest request) {
        UserEntity user = findById(id);
        UserEntity currentUser = currentUserProvider.getCurrent();
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("You cannot change your own role");
        }
        user.setRole(Role.valueOf(request.getRole()));
        userRepository.save(user);
    }

    private void verifyUserAccess(Long targetUserId) {
        UserEntity currentUser = currentUserProvider.getCurrent();
        if (!currentUser.getId().equals(targetUserId) && currentUser.getRole() != Role.SYS_ADMIN) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
