package com.whales.search.api;

import com.whales.search.domain.SearchHistory;

import java.util.UUID;

public record SearchHistoryResponse(
        UUID id,
        String keyword,
        String searchedAt
) {
    public static SearchHistoryResponse from(SearchHistory h) {
        return new SearchHistoryResponse(
                h.getId(),
                h.getKeyword(),
                h.getSearchedAt().toString()
        );
    }
}
