package com.xm.investment.recommendationservice.crypto.model;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public record Sample(Instant timestamp,
                     BigDecimal price) {

    public Sample {
        requireNonNull(timestamp);
        requireNonNull(price);
    }
}
