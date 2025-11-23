package com.whales.search.api;

import com.whales.post.api.PostResponse;
import com.whales.search.application.SearchService;
import com.whales.user.domain.User;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    /** 게시물 검색 */
    @GetMapping
    public List<PostResponse> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        UUID userId = (principal != null) ? principal.getId() : null;


        return searchService.search(keyword, userId)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    /** 검색 기록 조회 */
    @GetMapping("/history")
    public List<SearchHistoryResponse> history(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        if (principal == null) {
            // 비로그인인 경우: 검색 기록 없음
            return List.of();
        }

        UUID userId = principal.getId();

        return searchService.getHistory(userId)
                .stream()
                .map(SearchHistoryResponse::from)
                .toList();
    }
}
