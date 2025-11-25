package com.whales.search.api;

import com.whales.comment.domain.CommentRepository;
import com.whales.post.api.PostResponse;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.PostReactionService;
import com.whales.search.application.SearchService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "Search API", description = "검색 및 검색 기록 API")
public class SearchController {

    private final SearchService searchService;
    private final CommentRepository commentRepository;
    private final PostReactionService postReactionService;

    @Operation(
            summary = "게시물 검색",
            description = """
                    키워드를 기반으로 게시글을 검색합니다.  
                    반응 요약 정보(좋아요/싫어요)와 댓글 수(commentCount)를 포함한 PostResponse 리스트를 반환합니다.
                    """
    )
    @GetMapping
    public List<PostResponse> search(
            @Parameter(description = "검색 키워드")
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

    @Operation(
            summary = "검색 기록 조회",
            description = """
                    로그인한 사용자의 검색 기록을 최신순으로 반환합니다.  
                    비로그인 상태에서는 빈 리스트([])를 반환합니다.
                    """
    )
    @GetMapping("/history")
    public List<SearchHistoryResponse> history(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        if (principal == null) return List.of();

        return searchService.getHistory(principal.getId())
                .stream()
                .map(SearchHistoryResponse::from)
                .toList();
    }

    @Operation(
            summary = "검색 기록 단일 삭제",
            description = "특정 검색 기록(historyId)을 삭제합니다."
    )
    @DeleteMapping("/history/{historyId}")
    public void deleteHistory(
            @Parameter(description = "삭제할 검색 기록 ID")
            @PathVariable UUID historyId,

            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        if (principal == null) return;

        searchService.deleteHistory(historyId, principal.getId());
    }

    @Operation(
            summary = "검색 기록 전체 삭제",
            description = "로그인한 사용자의 모든 검색 기록을 삭제합니다."
    )
    @DeleteMapping("/history")
    public void clearHistory(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        if (principal == null) return;

        searchService.clearHistory(principal.getId());
    }
}