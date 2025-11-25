package com.whales.user.application;

import com.whales.user.api.UpdateProfileRequest;
import com.whales.user.api.UserResponse;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request != null) {

            // Display Name 변경 로직 (14일 쿨다운 적용)
            if (StringUtils.hasText(request.displayName())) {
                String newDisplayName = request.displayName().trim();

                if (!user.getDisplayName().equals(newDisplayName)) {
                    applyDisplayNameCooldown(user, newDisplayName);
                }
            }

            // 한 줄 소개(Bio) 변경 로직
            if (request.bio() != null) {
                String newBio = request.bio().trim();
                String currentBio = user.getBio() == null ? "" : user.getBio().trim();

                if (!currentBio.equals(newBio)) {

                    user.setBio(newBio.isEmpty() ? null : newBio);
                }
            }


            if (request.avatarUrl() != null) {
                String url = request.avatarUrl().trim();
                user.setAvatarUrl(url.isEmpty() ? null : url);
            }
        }

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    private void applyDisplayNameCooldown(User user, String newDisplayName) {
        final Duration cooldownPeriod = Duration.ofDays(14);
        Instant now = Instant.now();
        Instant lastChangeTime = user.getLastDisplayNameChange();

        if (lastChangeTime != null) {
            Instant nextChangeTime = lastChangeTime.plus(cooldownPeriod);

            if (now.isBefore(nextChangeTime)) {
                Duration remainingTime = Duration.between(now, nextChangeTime);
                long days = remainingTime.toDays();
                long hours = remainingTime.toHours() % 24;

                String message = String.format(
                        "Display name can only be changed once every 14 days. Remaining time: %d days %d hours.",
                        days, hours
                );

                throw new ResponseStatusException(HttpStatus.TOO_EARLY, message);
            }
        }


        user.setDisplayName(newDisplayName);
        user.setLastDisplayNameChange(now);
    }
}