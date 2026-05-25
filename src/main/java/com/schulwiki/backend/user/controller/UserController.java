package com.schulwiki.backend.user.controller;

import com.schulwiki.backend.error.MessageResponse;
import com.schulwiki.backend.role.RequireRole;
import com.schulwiki.backend.role.Role;
import com.schulwiki.backend.user.dto.*;
import com.schulwiki.backend.user.filter.UserFilter;
import com.schulwiki.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @RequireRole(Role.SYS_ADMIN)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    @RequireRole(Role.SYS_ADMIN)
    public ResponseEntity<Page<UserResponse>> getAllUsers(@ModelAttribute UserFilter filter, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(filter, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateProfile(@PathVariable Long id, @Valid @RequestBody UserProfileUpdateRequest request) {
        userService.updateUserProfile(id, request);
        return ResponseEntity.ok(new MessageResponse("User profile updated successfully"));
    }

    @PatchMapping("/{id}/identity")
    public ResponseEntity<MessageResponse> updateIdentity(@PathVariable Long id, @Valid @RequestBody UserIdentityUpdateRequest request) {
        userService.updateUserIdentity(id, request);
        return ResponseEntity.ok(new MessageResponse("User identity updated successfully"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<MessageResponse> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordUpdateRequest request) {
        userService.updateUserPassword(id, request);
        return ResponseEntity.ok(new MessageResponse("User password updated successfully"));
    }


    @PatchMapping("/{id}/role")
    @RequireRole(Role.SYS_ADMIN)
    public ResponseEntity<MessageResponse> updateRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateRequest request) {
        userService.updateUserRole(id, request);
        return ResponseEntity.ok(new MessageResponse("User role updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }
}
