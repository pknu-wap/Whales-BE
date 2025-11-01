package com.whales.reaction.application;

import com.whales.reaction.api.ReactionRequest;
import com.whales.reaction.api.ReactionResponse;
import com.whales.reaction.domain.Reaction;
import com.whales.reaction.domain.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;

    // (TODO: Post, Comment의 유효성 검사 로직 및 신뢰도/카운트 업데이트 로직은 추가 구현 필요)

    /**
     * 게시글에 대한 좋아요/싫어요를 설정하거나 변경합니다. (PUT)
     * @param userId 요청 사용자 ID
     * @param postId 대상 게시글 ID
     * @param request 좋아요 타입 (LIKE/DISLIKE)
     * @return ReactionResponse (생성 또는 변경된 Reaction ID)
     */
    @Transactional
    public ReactionResponse setPostReaction(UUID userId, UUID postId, ReactionRequest request) {
        Reaction.ReactionType newType = request.type();

        // 1. 기존 반응을 찾습니다.
        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserIdAndPostId(userId, postId);

        if (existingReactionOpt.isPresent()) {
            // 2. 기존 반응이 있으면: 타입 변경
            Reaction existingReaction = existingReactionOpt.get();

            // 타입 변경 로직 호출 (Post Reaction Count 업데이트 로직 포함)
            existingReaction.changeReactionType(newType);

            // TODO: Post의 Reaction Count 변경 로직 추가 (oldType -1, newType +1)

            return ReactionResponse.builder()
                    .id(existingReaction.getId())
                    .type(newType.name())
                    .message("Reaction type updated successfully.")
                    .build();
        } else {
            // 3. 기존 반응이 없으면: 새로 생성
            Reaction newReaction = Reaction.builder()
                    .userId(userId)
                    .postId(postId)
                    .type(newType)
                    .build();
            Reaction savedReaction = reactionRepository.save(newReaction);

            // TODO: Post의 Reaction Count 증가 로직 추가 (+1)

            return ReactionResponse.builder()
                    .id(savedReaction.getId())
                    .type(newType.name())
                    .message("Reaction created successfully.")
                    .build();
        }
    }

    /**
     * 게시글에 대한 좋아요/싫어요를 삭제합니다. (DELETE)
     * @param userId 요청 사용자 ID
     * @param postId 대상 게시글 ID
     */
    @Transactional
    public void removePostReaction(UUID userId, UUID postId) {
        // 1. 삭제할 반응을 찾습니다.
        Reaction existingReaction = reactionRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found for post or already removed."));

        // 2. 삭제 (Post Reaction Count 업데이트 로직 포함)
        reactionRepository.delete(existingReaction);

        // TODO: Post의 Reaction Count 감소 로직 추가 (-1)
    }


    /**
     * 댓글에 대한 좋아요/싫어요를 설정하거나 변경합니다. (PUT)
     * @param userId 요청 사용자 ID
     * @param commentId 대상 댓글 ID
     * @param request 좋아요 타입 (LIKE/DISLIKE)
     * @return ReactionResponse (생성 또는 변경된 Reaction ID)
     */
    @Transactional
    public ReactionResponse setCommentReaction(UUID userId, UUID commentId, ReactionRequest request) {
        Reaction.ReactionType newType = request.type();

        // 1. 기존 반응을 찾습니다.
        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingReactionOpt.isPresent()) {
            // 2. 기존 반응이 있으면: 타입 변경
            Reaction existingReaction = existingReactionOpt.get();

            existingReaction.changeReactionType(newType);

            // TODO: Comment의 Reaction Count 변경 로직 추가 (oldType -1, newType +1)

            return ReactionResponse.builder()
                    .id(existingReaction.getId())
                    .type(newType.name())
                    .message("Reaction type updated successfully.")
                    .build();
        } else {
            // 3. 기존 반응이 없으면: 새로 생성
            Reaction newReaction = Reaction.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .type(newType)
                    .build();
            Reaction savedReaction = reactionRepository.save(newReaction);

            // TODO: Comment의 Reaction Count 증가 로직 추가 (+1)

            return ReactionResponse.builder()
                    .id(savedReaction.getId())
                    .type(newType.name())
                    .message("Reaction created successfully.")
                    .build();
        }
    }

    /**
     * 댓글에 대한 좋아요/싫어요를 삭제합니다. (DELETE)
     * @param userId 요청 사용자 ID
     * @param commentId 대상 댓글 ID
     */
    @Transactional
    public void removeCommentReaction(UUID userId, UUID commentId) {
        // 1. 삭제할 반응을 찾습니다.
        Reaction existingReaction = reactionRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found for comment or already removed."));

        // 2. 삭제
        reactionRepository.delete(existingReaction);

        // TODO: Comment의 Reaction Count 감소 로직 추가 (-1)
    }
}