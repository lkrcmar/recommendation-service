package com.xm.investment.recommendationservice.crypto.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public record CryptoCsvRow(@JsonProperty(required = true) long timestamp,
                           @JsonProperty(required = true) String symbol,
                           @JsonProperty(required = true) BigDecimal price) {

    public CryptoCsvRow {
        if (timestamp <= 0) {
            throw new IllegalArgumentException("Invalid timestamp '%s'".formatted(timestamp));
        }
        requireNonNull(symbol);
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("Invalid symbol '%s'".formatted(symbol));
        }
        requireNonNull(price);
    }
}
