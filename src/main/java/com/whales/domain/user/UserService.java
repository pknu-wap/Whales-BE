package com.whales.domain.user;

import com.whales.api.dto.request.UpdateProfileRequest;
import com.whales.api.dto.response.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MeResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return toMeResponse(user);
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
                user.setAvatarUrl(url.isEmpty() ? null : url);
            }
        }

        User savedUser = userRepository.save(user);

        return toMeResponse(savedUser);
    }

    private MeResponse toMeResponse(User u) {
        return new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getNicknameColor(),
                u.getStatus().name(),
                u.getAvatarUrl()
        );
    }
}
