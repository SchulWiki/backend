package com.schulwiki.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    @Size(min = 1, message = "First name must not be blank")
    private String firstName;

    @Size(min = 1, message = "Last name must not be blank")
    private String lastName;
}
