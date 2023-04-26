package com.xm.investment.recommendationservice.crypto;

import com.xm.investment.recommendationservice.crypto.model.DateRange;
import com.xm.investment.recommendationservice.crypto.model.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "crypto.repository.path=./data-test/prices")
public class StatististicsCalculatorTest {

    @Autowired
    StatisticsCalculator statisticsCalculator;

    @Test
    void calculate_repeatedCalculation_cacheHit() {
        Instant from = Instant.ofEpochMilli(1641009600000L);
        Instant to = Instant.ofEpochMilli(1641009601000L);
        Optional<Statistics> first = statisticsCalculator.calculate(
                "BTC",
                new DateRange(from, to));
        Optional<Statistics> second = statisticsCalculator.calculate(
                "BTC",
                new DateRange(from, to));
        assertThat(first)
                .get()
                .isSameAs(second.orElseThrow());
    }
}
