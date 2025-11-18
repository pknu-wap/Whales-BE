package com.whales.favorite.application;

import com.whales.favorite.domain.FavoriteTag;
import com.whales.favorite.domain.FavoriteTagRepository;
import com.whales.tag.domain.Tag;
import com.whales.tag.domain.TagRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteTagService {

    private final FavoriteTagRepository favoriteTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFavoriteTag(UUID userId, String tagName) {

        // 태그 확인
        Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));

        // 이미 즐겨찾기에 있음 → 예외
        if (favoriteTagRepository.existsByUser_IdAndTag_Id(userId, tag.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists");
        }

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 저장
        favoriteTagRepository.save(new FavoriteTag(user, tag));
    }

    @Transactional
    public void removeFavoriteTag(UUID userId, UUID tagId) {
        favoriteTagRepository.deleteByUser_IdAndTag_Id(userId,tagId);
    }

    public List<Tag> getMyFavoriteTags(UUID userId) {
        return favoriteTagRepository.findByUser_Id(userId).stream()
                .map(FavoriteTag::getTag)
                .collect(Collectors.toList());
    }
}
