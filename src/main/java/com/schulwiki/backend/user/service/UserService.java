package com.schulwiki.backend.user.service;

import com.schulwiki.backend.error.exception.NotFoundException;
import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
