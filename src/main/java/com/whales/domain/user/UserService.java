package com.whales.domain.user;

import com.whales.api.dto.request.UpdateProfileRequest;
import com.whales.api.dto.response.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MeResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new MeResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getNicknameColor(), user.getStatus().name(), user.getAvatarUrl());
    }

    @Transactional
    public MeResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request != null) {
            if (StringUtils.hasText(request.displayName())) {
                user.setDisplayName(request.displayName().trim());
            }
            if (request.avatarUrl() != null) {
                String url = request.avatarUrl().trim();
                user.setAvatarUrl(url);
            }
        }

        User savedUser = userRepository.save(user);

        return new MeResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getDisplayName(), savedUser.getNicknameColor(), savedUser.getStatus().name(), user.getAvatarUrl());
    }
}
