package com.schulwiki.backend.role;

import lombok.Getter;

@Getter
public enum Role {
    SYS_ADMIN(4),
    ADMIN(3),
    EDITOR(2),
    GUEST(1);

    private final int weight;
    Role(int weight) {
        this.weight = weight;
    }
}
