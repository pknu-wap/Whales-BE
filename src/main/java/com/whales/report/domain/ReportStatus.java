package com.whales.report.domain;

public enum ReportStatus {
    PENDING,   // 처리 대기
    ACCEPTED,  // 신고 수리됨 (콘텐츠 조치됨)
    REJECTED   // 신고 반려됨
}
