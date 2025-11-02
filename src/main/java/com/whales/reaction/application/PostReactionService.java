package com.whales.reaction.application;

import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.reaction.domain.PostReaction;
import com.whales.reaction.domain.PostReactionRepository;
import com.whales.reaction.domain.ReactionType;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostReactionService extends ReactionService<PostReaction> {

    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggle(UUID postId, UUID userId, ReactionType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        Optional<PostReaction> existing = postReactionRepository.findByUserIdAndPostId(userId, postId);
        toggleReaction(existing, new PostReaction(user, post, type), type);
    }

    @Override
    protected Optional<PostReaction> findReactionByUserAndTargetId(UUID userId, UUID postId) {
        return postReactionRepository.findByUserIdAndPostId(userId, postId);
    }

    @Override
    protected void saveReaction(PostReaction reaction) {
        postReactionRepository.save(reaction);
    }

    @Override
    protected void deleteReaction(PostReaction reaction) {
        postReactionRepository.delete(reaction);
    }

}
