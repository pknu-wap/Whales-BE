package com.whales.user.application;

import com.whales.user.api.UpdateProfileRequest;
import com.whales.user.api.MeResponse;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MeResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return MeResponse.from(user);
    }

    @Transactional
    public MeResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request != null) {
            if (StringUtils.hasText(request.displayName())) {
                user.setDisplayName(request.displayName().trim());
            }
            if (request.avatarUrl() != null) {
                String url = request.avatarUrl().trim();
                user.setAvatarUrl(url.isEmpty() ? null : url);
            }
        }

        User savedUser = userRepository.save(user);

        return MeResponse.from(savedUser);
    }
}
