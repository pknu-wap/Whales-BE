package com.whales.common;

public enum ContentStatus {
    ACTIVE,     // 정상 노출
    BLOCKED,    // 차단/블라인드 (신고 등)
    DELETED     // 사용자가 삭제 (soft delete)
}