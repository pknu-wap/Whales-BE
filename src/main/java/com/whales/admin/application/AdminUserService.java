package com.whales.admin.application;

import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import com.whales.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional
    public void banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setStatus(UserStatus.BANNED);
    }


    @Transactional
    public void unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setStatus(UserStatus.ACTIVE);
    }
}
