package br.com.businfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CacheStats(
        int entries,
        long hits,
        long misses,
        @JsonProperty("ttl_seconds") long ttlSeconds) {
}
