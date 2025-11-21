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

        // 기록 저장
        saveSearchHistory(rawKeyword, user);

        String keyword = keywordParser.normalizeKeyword(rawKeyword);

        // #태그 검색
        if (keywordParser.isTagSearch(keyword)) {
            List<String> tags = keywordParser.extractTags(keyword);
            return postRepository.findPostsByAllTagNames(tags, tags.size());
        }

        // 제목 or 내용 검색
        return postRepository.searchByKeyword(keyword);
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

    /** 검색 기록 조회 */
    public List<SearchHistory> getHistory(User user) {
        return historyRepository.findByUserOrderBySearchedAtDesc(user);
    }
}
