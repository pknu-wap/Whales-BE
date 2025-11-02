package com.whales.scrap.application;

import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.scrap.domain.PostScrap;
import com.whales.scrap.domain.PostScrapRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostScrapService {

    private final PostScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggle(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        scrapRepository.findByUser_IdAndPost_Id(userId, postId)
                .ifPresentOrElse(
                        scrapRepository::delete,   // 이미 있으면 삭제 (스크랩 해제)
                        () -> scrapRepository.save(new PostScrap(user, post)) // 없으면 생성
                );
    }

    @Transactional(readOnly = true)
    public boolean isScrapped(UUID postId, UUID userId) {
        return scrapRepository.existsByUser_IdAndPost_Id(userId, postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getMyScraps(UUID userId) {
        List<PostScrap> scraps = scrapRepository.findByUser_Id(userId);
        return scraps.stream()
                .map(PostScrap::getPost)
                .toList();
    }
}