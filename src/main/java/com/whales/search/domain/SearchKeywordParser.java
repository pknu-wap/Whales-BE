package com.whales.search.domain;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SearchKeywordParser {

    public boolean isTagSearch(String keyword) {
        return keyword.trim().startsWith("#");
    }

    public List<String> extractTags(String keyword) {
        return Arrays.stream(keyword.split("#"))
                .filter(s -> !s.isBlank())
                .map(s -> s.trim().toLowerCase())
                .toList();
    }

    public String normalizeKeyword(String keyword) {
        return keyword.trim().toLowerCase();
    }
}
