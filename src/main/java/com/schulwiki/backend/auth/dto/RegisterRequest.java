package com.schulwiki.backend.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    @Valid
    private ValidationCredentialsRequest validationCredentialsRequest;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email can be up to 100 characters")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name can be up to 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name can be up to 100 characters")
    private String lastName;
}
