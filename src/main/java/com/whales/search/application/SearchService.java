package com.whales.search.application;

import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.search.domain.SearchHistory;
import com.whales.search.domain.SearchHistoryRepository;
import com.whales.search.domain.SearchKeywordParser;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final SearchHistoryRepository historyRepository;
    private final SearchKeywordParser keywordParser;
    private final UserRepository userRepository;

    public List<Post> search(String rawKeyword, UUID userId){


        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            // 로그인 한 경우에만 검색 기록 저장
            saveSearchHistory(rawKeyword, user);
        }

        // 정규화
        String normalized = keywordParser.normalizeKeyword(rawKeyword);

        // 태그 + 일반 키워드 분리
        List<String> tags = keywordParser.extractTags(normalized);
        String textKeyword = keywordParser.extractNormalKeyword(normalized);

        boolean hasTags = !tags.isEmpty();
        boolean hasText = textKeyword != null && !textKeyword.isBlank();

        // 1) 태그 + 일반 키워드 조합 검색
        if (hasTags && hasText) {
            return postRepository.searchByTagsAndKeyword(tags, tags.size(), textKeyword);
        }

        // 2) 태그만 검색
        if (hasTags) {
            return postRepository.findPostsByAllTagNames(tags, tags.size());
        }

        // 3) 일반 키워드만 검색
        return postRepository.searchByKeyword(textKeyword);
    }

    /** 검색 기록 저장 */
    private void saveSearchHistory(String keyword, User user) {
        historyRepository.findByUserAndKeyword(user, keyword)
                .ifPresentOrElse(
                        exist -> exist.setSearchedAt(LocalDateTime.now()),
                        () -> {
                            SearchHistory h = new SearchHistory();
                            h.setUser(user);
                            h.setKeyword(keyword);
                            h.setSearchedAt(LocalDateTime.now());
                            historyRepository.save(h);
                        }
                );
    }

    public List<SearchHistory> getHistory(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return historyRepository.findByUserOrderBySearchedAtDesc(user);
    }
    /** 검색 기록 단일 삭제 */
    public void deleteHistory(UUID historyId, UUID userId) {
        SearchHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("History not found: " + historyId));

        if (!history.getUser().getId().equals(userId)) {
            // 다른 사람 기록 삭제 방지
            throw new IllegalArgumentException("Not allowed to delete this history");
        }

        historyRepository.delete(history);
    }

    /** 검색 기록 전체 삭제 */
    @Transactional
    public void clearHistory(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        historyRepository.deleteAllByUser_Id(userId);
    }
}
