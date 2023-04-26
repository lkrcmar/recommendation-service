package com.xm.investment.recommendationservice.crypto;

import com.xm.investment.recommendationservice.crypto.model.DateRange;
import com.xm.investment.recommendationservice.crypto.model.Sample;
import com.xm.investment.recommendationservice.crypto.model.Statistics;
import com.xm.investment.recommendationservice.crypto.repository.CryptoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@Component
public class StatisticsCalculator {

    private final CryptoRepository repository;

    public StatisticsCalculator(CryptoRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = "statistics")
    public Optional<Statistics> calculate(String symbol, DateRange range) {
        return calculateStatistics(repository.samples(symbol, range));
    }

    static Optional<Statistics> calculateStatistics(SortedMap<Instant, BigDecimal> samples) {
        if (samples.isEmpty()) {
            return Optional.empty();
        }

        Sample oldest = new Sample(samples.firstKey(), samples.get(samples.firstKey()));
        Sample newest = new Sample(samples.lastKey(), samples.get(samples.lastKey()));
        Sample min = oldest;
        Sample max = oldest;

        for (Map.Entry<Instant, BigDecimal> e : samples.entrySet()) {
            if (e.getValue().compareTo(min.price()) < 0) {
                min = new Sample(e.getKey(), e.getValue());
            }
            if (e.getValue().compareTo(max.price()) > 0) {
                max = new Sample(e.getKey(), e.getValue());
            }
        }

        BigDecimal normalizedRange = max.price().subtract(min.price()).divide(min.price(), RoundingMode.HALF_UP);

        return Optional.of(new Statistics(oldest, newest, min, max, normalizedRange));
    }
}
