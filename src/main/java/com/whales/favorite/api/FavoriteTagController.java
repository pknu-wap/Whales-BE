package com.whales.favorite.api;

import com.whales.favorite.application.FavoriteTagService;
import com.whales.security.WhalesUserPrincipal;
import com.whales.tag.api.TagResponse;
import com.whales.tag.domain.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags/favorites")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Favorite Tag API", description = "사용자 관심 태그(즐겨찾기 태그) 관리 API")
public class FavoriteTagController {

    private final FavoriteTagService favoriteTagService;

    @Operation(
            summary = "관심 태그 추가",
            description = """
                    사용자의 관심 태그 목록에 새로운 태그를 추가합니다.
                    이미 존재하는 태그면 예외가 발생합니다.
                    """
    )
    @PostMapping
    public ResponseEntity<Void> addFavoriteTag(
            @Parameter(description = "추가할 관심태그 요청 DTO")
            @RequestBody FavoriteTagRequest request,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        favoriteTagService.addFavoriteTag(principal.getId(), request.name());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "관심 태그 삭제",
            description = "사용자의 관심 태그 목록에서 특정 태그를 제거합니다."
    )
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteFavoriteTag(
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @Parameter(description = "삭제할 태그 ID")
            @PathVariable UUID tagId
    ) {
        favoriteTagService.removeFavoriteTag(principal.getId(), tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "내 관심 태그 조회",
            description = "현재 로그인한 사용자의 관심 태그 목록을 반환합니다."
    )
    @GetMapping
    public ResponseEntity<List<TagResponse>> getMyFavoriteTags(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        List<Tag> tags = favoriteTagService.getMyFavoriteTags(principal.getId());

        List<TagResponse> response = tags.stream()
                .map(TagResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}