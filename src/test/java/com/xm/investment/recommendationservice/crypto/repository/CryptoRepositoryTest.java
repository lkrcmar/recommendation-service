package com.xm.investment.recommendationservice.crypto.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoRepositoryTest {

    @Test
    void init_twoSymbolsInRepository_symbolToSamplesCreatedAndContainsTwoSymbolsWithSortedSamples()
            throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository(
                new CryptoRepository.Properties(Path.of("./data-test/prices")));
        Map<String, CryptoRepository.TimeSeries> symbolToSamples = cryptoRepository.symbolToTimeSeries;

        assertThat(symbolToSamples)
                .containsOnlyKeys("BTC", "ETH");
        assertThat(symbolToSamples.get("BTC").samples())
                .containsExactly(
                        Map.entry(
                                Instant.parse("2022-01-01T04:00:00Z"),
                                new BigDecimal("200.01")),
                        Map.entry(
                                Instant.parse("2022-01-01T04:00:01Z"),
                                new BigDecimal("201")),
                        Map.entry(
                                Instant.parse("2022-01-01T04:00:02Z"),
                                new BigDecimal("200.1"))
                );
        assertThat(symbolToSamples.get("ETH").samples())
                .containsExactly(
                        Map.entry(
                                Instant.parse("2022-01-02T11:46:40Z"),
                                new BigDecimal("100.32"))
                );
    }

    @Test
    void symbols_twoSymbolsInRepository_exactlyTwoSymbolsReturned() throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository(
                new CryptoRepository.Properties(Path.of("./data-test/prices")));
        assertThat(cryptoRepository.symbols())
                .containsExactly("BTC", "ETH");
    }
}
