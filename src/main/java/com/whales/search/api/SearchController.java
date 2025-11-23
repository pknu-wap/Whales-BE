package com.whales.search.api;

import com.whales.comment.domain.CommentRepository;
import com.whales.post.api.PostResponse;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.PostReactionService;
import com.whales.search.application.SearchService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final CommentRepository commentRepository;
    private final PostReactionService postReactionService;

    /** 게시물 검색 */
    @GetMapping
    public List<PostResponse> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        UUID userId = (principal != null) ? principal.getId() : null;


        return searchService.search(keyword, userId)
                .stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPost_Id(post.getId());
                    ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(post.getId());

                    return PostResponse.from(post, commentCount, reactions);
                })
                .collect(Collectors.toList());
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
