package com.whales.report.application;

import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.post.domain.PostRepository;
import com.whales.report.domain.ReportRepository;
import com.whales.report.domain.ReportStatus;
import com.whales.user.domain.UserBadgeColor;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Long> getSummary() {
        Map<String, Long> map = new HashMap<>();
        map.put("pending", reportRepository.countByStatus(ReportStatus.PENDING));
        map.put("accepted", reportRepository.countByStatus(ReportStatus.ACCEPTED));
        map.put("rejected", reportRepository.countByStatus(ReportStatus.REJECTED));
        map.put("blockedPosts", postRepository.countByStatus(ContentStatus.BLOCKED));
        map.put("blockedComments", commentRepository.countByStatus(ContentStatus.BLOCKED));
        map.put("orangeUsers", userRepository.countByBadgeColor(UserBadgeColor.ORANGE));
        map.put("redUsers", userRepository.countByBadgeColor(UserBadgeColor.RED));
        return map;
    }
}