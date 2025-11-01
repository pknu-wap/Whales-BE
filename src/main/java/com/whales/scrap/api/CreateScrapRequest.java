package com.whales.scrap.api;

import java.util.UUID;

/**
 * 스크랩 생성/취소 요청 DTO (record 버전)
 */
public record CreateScrapRequest(UUID postId, UUID commentId) {

    /**
     * 유효성 검사: postId 또는 commentId 중 하나만 존재해야 함
     */
    public boolean isValid() {
        return (postId != null) ^ (commentId != null); // XOR
    }
}
