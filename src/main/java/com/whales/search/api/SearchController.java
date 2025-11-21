package com.whales.search.api;

import com.whales.post.api.PostResponse;
import com.whales.search.application.SearchService;
import com.whales.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    /** 게시물 검색 */
    @GetMapping
    public List<PostResponse> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user
    ) {
        return searchService.search(keyword, user)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    /** 검색 기록 조회 */
    @GetMapping("/history")
    public List<SearchHistoryResponse> history(
            @AuthenticationPrincipal User user
    ) {
        return searchService.getHistory(user)
                .stream()
                .map(SearchHistoryResponse::from)
                .toList();
    }
}
