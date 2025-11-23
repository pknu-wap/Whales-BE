package com.whales.search.application;

import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.search.domain.SearchHistory;
import com.whales.search.domain.SearchHistoryRepository;
import com.whales.search.domain.SearchKeywordParser;
import com.whales.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final SearchHistoryRepository historyRepository;
    private final SearchKeywordParser keywordParser;

    public List<Post> search(String rawKeyword, User user) {

        saveSearchHistory(rawKeyword, user);

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

    public List<SearchHistory> getHistory(User user) {
        return historyRepository.findByUserOrderBySearchedAtDesc(user);
    }
}
