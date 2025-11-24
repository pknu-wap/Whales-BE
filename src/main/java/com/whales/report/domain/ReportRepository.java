package com.whales.report.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    // 특정 대상(Post or Comment)에 대한 모든 신고
    List<Report> findByTargetId(UUID targetId);

    // 미처리 신고 목록
    List<Report> findByStatus(ReportStatus status);

    // 특정 유저가 신고한 기록
    List<Report> findByReporter_Id(UUID userId);
}
