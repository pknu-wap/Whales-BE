package com.whales.scrap.application;

import com.whales.scrap.domain.Scrap;
import com.whales.scrap.domain.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;

    /**
     * 게시글 또는 댓글에 대한 스크랩 상태를 토글합니다. (스크랩 <-> 취소)
     */
    @Transactional
    public boolean toggleScrap(UUID userId, UUID postId, UUID commentId) {
        if ((postId == null) == (commentId == null)) {
            throw new IllegalArgumentException("Post ID or Comment ID must be provided, but not both.");
        }

        Optional<Scrap> existingScrap = (postId != null)
                ? scrapRepository.findByUserIdAndPostId(userId, postId)
                : scrapRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingScrap.isPresent()) {
            scrapRepository.delete(existingScrap.get());
            return false; // 스크랩 취소됨
        }

        Scrap newScrap = Scrap.builder()
                .userId(userId)
                .postId(postId)
                .commentId(commentId)
                .build();

        scrapRepository.save(newScrap);
        return true; // 스크랩 생성됨
    }
}
