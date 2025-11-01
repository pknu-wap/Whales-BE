package com.whales.scrap.api;

import com.whales.scrap.application.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 스크랩 기능 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    // (임시) 인증 전 사용자 ID
    private static final UUID TEMP_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /**
     * 게시글 또는 댓글에 대한 스크랩 상태를 토글합니다.
     * 생성되면 201 Created, 취소되면 200 OK 반환.
     */
    @PostMapping
    public ResponseEntity<ScrapResponse> toggleScrap(@RequestBody CreateScrapRequest request) {

        if (!request.isValid()) {
            return ResponseEntity.badRequest().build();
        }

        boolean isScrapped = scrapService.toggleScrap(
                TEMP_USER_ID,
                request.postId(),
                request.commentId()
        );

        ScrapResponse response = ScrapResponse.of(
                TEMP_USER_ID,
                request.postId(),
                request.commentId(),
                isScrapped
        );

        HttpStatus status = isScrapped ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
