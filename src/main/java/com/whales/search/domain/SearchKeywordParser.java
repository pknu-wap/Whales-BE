package com.whales.search.domain;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SearchKeywordParser {

    public boolean isTag(String part) {
        return part.startsWith("#");
    }

    public List<String> extractTags(String normalized) {
        return Arrays.stream(normalized.split("\\s+"))
                .filter(p -> p.startsWith("#"))
                .map(p -> p.replace("#", "").trim().toLowerCase())
                .filter(p -> !p.isBlank())
                .toList();
    }

    public String extractNormalKeyword(String normalized) {
        return Arrays.stream(normalized.split("\\s+"))
                .filter(p -> !p.startsWith("#"))
                .reduce("", (a, b) -> a + " " + b)
                .trim();
    }

    public String normalizeKeyword(String keyword) {
        return keyword.trim().toLowerCase();
    }
}
