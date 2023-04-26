package com.xm.investment.recommendationservice.crypto.repository;

import com.xm.investment.recommendationservice.crypto.model.DateRange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@EnableConfigurationProperties(CryptoRepository.Properties.class)
@Component
public class CryptoRepository {

    final Map<String, TimeSeries> symbolToTimeSeries;

    public CryptoRepository(Properties properties) throws IOException {
        try (Stream<Path> paths = Files.walk(properties.path)) {
            symbolToTimeSeries = paths
                    .filter(Files::isRegularFile)
                    .map(CryptoCsvFileReader::readCryptoCsvRows)
                    .flatMap(Collection::stream)
                    .collect(groupingBy(
                            CryptoCsvRow::symbol,
                            collectingAndThen(
                                    toMap(
                                            cryptoCsvRow -> Instant.ofEpochMilli(cryptoCsvRow.timestamp()),
                                            CryptoCsvRow::price,
                                            throwOnDuplicity(),
                                            TreeMap::new
                                    ),
                                    TimeSeries::new
                            )));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public Set<String> symbols() {
        return symbolToTimeSeries.keySet();
    }

    public SortedMap<Instant, BigDecimal> samples(String symbol, DateRange dateRange) {
        TimeSeries timeSeries = symbolToTimeSeries.getOrDefault(symbol, TimeSeries.EMPTY);
        return timeSeries.samples.subMap(dateRange.from(), dateRange.to());
    }

    private static BinaryOperator<BigDecimal> throwOnDuplicity() {
        return (a, b) -> {
            throw new RuntimeException("Duplicity in timestamps");
        };
    }

    record TimeSeries(SortedMap<Instant, BigDecimal> samples) {

        public static final TimeSeries EMPTY = new TimeSeries(Collections.emptySortedMap());
    }

    @ConfigurationProperties(prefix = "crypto.repository")
    public record Properties(Path path) {}

}
