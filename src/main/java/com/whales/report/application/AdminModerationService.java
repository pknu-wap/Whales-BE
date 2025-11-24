package com.whales.report.application;

import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminModerationService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Post> getBlockedPosts() {
        return postRepository.findByStatus(ContentStatus.BLOCKED);
    }

    @Transactional(readOnly = true)
    public List<Comment> getBlockedComments() {
        return commentRepository.findByStatus(ContentStatus.BLOCKED);
    }

    @Transactional(readOnly = true)
    public List<User> findUsersByBadgeColor(UserBadgeColor badgeColor) {
        return userRepository.findByBadgeColor(badgeColor);
    }
}
