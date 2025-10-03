package com.whales.tag.application;

import com.whales.post.api.PostResponse;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.tag.api.TagListRequest;
import com.whales.tag.api.TagRequest;
import com.whales.tag.api.TagResponse;
import com.whales.tag.domain.PostTag;
import com.whales.tag.domain.PostTagRepository;
import com.whales.tag.domain.Tag;
import com.whales.tag.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;

    // 게시글에 연결된 태그 조회
    public List<TagResponse> listByPost(UUID postId) {
        return postTagRepository.findByPostId(postId).stream()
                .map(PostTag::getTag)
                .map(t -> new TagResponse(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }

    // 태그 일괄 추가 (중복 무시) - 작성자 권한 확인
    @Transactional
    public List<TagResponse> addTags(UUID postId, UUID authorId, TagListRequest request) {
        Post post = loadPostWithAuth(postId, authorId);

        // 요청 내 중복 제거 + 정규화(lowercase)
        Set<String> names = normalize(request.tags());

        List<TagResponse> result = new ArrayList<>();
        for (String name : names) {
            Tag tag = tagRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(name);
                        return tagRepository.save(t);
                    });

            if (!postTagRepository.existsByPostIdAndTagId(postId, tag.getId())) {
                PostTag link = new PostTag(post, tag);
                postTagRepository.save(link);
            }
            result.add(new TagResponse(tag.getId(), tag.getName()));
        }
        return result;
    }

    // 태그 단건 추가
    @Transactional
    public TagResponse addOneTag(UUID postId, UUID authorId, TagRequest request) {
        Post post = loadPostWithAuth(postId, authorId);
        String normalized = normalizeOne(request.name());

        Tag tag = tagRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Tag t = new Tag();
                    t.setName(normalized);
                    return tagRepository.save(t);
                });

        if (!postTagRepository.existsByPostIdAndTagId(postId, tag.getId())) {
            PostTag link = new PostTag(post, tag);
            postTagRepository.save(link);
        }
        return new TagResponse(tag.getId(), tag.getName());
    }

    // 태그 단건 제거
    @Transactional
    public void removeOneTag(UUID postId, UUID authorId, UUID tagId) {
        loadPostWithAuth(postId, authorId); // 권한 체크 겸 존재 확인
        postTagRepository.deleteByPostIdAndTagId(postId, tagId);
    }

    // 태그 전체 교체(덮어쓰기)
    @Transactional
    public List<TagResponse> replaceAllTags(UUID postId, UUID authorId, TagListRequest request) {
        Post post = loadPostWithAuth(postId, authorId);

        // 모두 제거 후 새로 생성/연결
        postTagRepository.deleteAllByPostId(postId);
        return addTags(postId, authorId, request);
    }

    // 태그로 게시글 검색
    public List<PostResponse> getPostsByTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }

        // 소문자 trim 처리
        List<String> normalized = tagNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .toList();

        List<Post> posts = postRepository.findPostsByAllTagNames(normalized, normalized.size());
        return posts.stream()
                .map(PostResponse::from)
                .toList();
    }

    // ---------- helpers ----------

    private Post loadPostWithAuth(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can manage tags");
        }
        return post;
    }

    private Set<String> normalize(List<String> raw) {
        return raw.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)  // 검색/중복 방지 일관성
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeOne(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name required");
        }
        return name.trim().toLowerCase();
    }
}
