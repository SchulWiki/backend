package com.schulwiki.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleUpdateRequest {
    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "^(SYS_ADMIN|ADMIN|EDITOR|GUEST)$", message = "Invalid role provided")
    private String role;
}
