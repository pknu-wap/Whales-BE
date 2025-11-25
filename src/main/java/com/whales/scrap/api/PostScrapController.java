package com.whales.scrap.api;

import com.whales.comment.domain.CommentRepository;
import com.whales.post.api.PostResponse;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.PostReactionService;
import com.whales.scrap.application.PostScrapService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Post Scrap API", description = "게시글 스크랩 기능 API")
public class PostScrapController {

    private final PostScrapService postScrapService;
    private final CommentRepository commentRepository;
    private final PostReactionService postReactionService;

    @Operation(
            summary = "게시글 스크랩 토글",
            description = """
                    사용자가 특정 게시글을 스크랩/스크랩 취소합니다.
                    • 스크랩 O → 스크랩 취소  
                    • 스크랩 X → 스크랩 등록  
                    """
    )
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<Void> toggleScrap(
            @Parameter(description = "스크랩할 게시글 ID")
            @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        postScrapService.toggle(postId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "해당 게시글 스크랩 여부 조회",
            description = "현재 로그인한 사용자가 특정 게시글을 스크랩했는지 여부를 반환합니다."
    )
    @GetMapping("/{postId}/scrap")
    public ResponseEntity<Boolean> isScrapped(
            @Parameter(description = "스크랩 여부를 확인할 게시글 ID")
            @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        Boolean scrapped = postScrapService.isScrapped(postId, principal.getId());
        return ResponseEntity.ok(scrapped);
    }

    @Operation(
            summary = "내가 스크랩한 게시글 목록 조회",
            description = """
                    현재 사용자가 스크랩한 게시글 목록을 반환합니다.  
                    게시글별로 댓글 수(commentCount)와 좋아요/싫어요 요약(reactions)도 함께 포함됩니다.
                    """
    )
    @GetMapping("/scraps")
    public ResponseEntity<List<PostResponse>> getMyScraps(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        List<PostResponse> myScraps = postScrapService.getMyScraps(principal.getId())
                .stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPost_Id(post.getId());
                    ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(post.getId());
                    return PostResponse.from(post, commentCount, reactions);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(myScraps);
    }
}