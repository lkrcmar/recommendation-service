package com.xm.investment.recommendationservice.crypto.model;


import java.time.Instant;

public record DateRange(Instant from, Instant to) {

    public static final DateRange UNLIMITED = new DateRange(Instant.MIN, Instant.MAX);

}
