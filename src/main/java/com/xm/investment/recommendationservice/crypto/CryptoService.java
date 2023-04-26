package com.xm.investment.recommendationservice.crypto;

import com.xm.investment.recommendationservice.crypto.model.DateRange;
import com.xm.investment.recommendationservice.crypto.model.Statistics;
import com.xm.investment.recommendationservice.crypto.repository.CryptoRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Service
public class CryptoService {

    private final CryptoRepository repository;

    private final StatisticsCalculator statisticsCalculator;

    public CryptoService(CryptoRepository repository, StatisticsCalculator statisticsCalculator) {
        this.repository = repository;
        this.statisticsCalculator = statisticsCalculator;
    }

    public Map<String, Statistics> getStatistics(DateRange range) {
        return repository.symbols().stream()
                .flatMap(symbol -> getStatistics(symbol, range).stream()
                        .map(statistics -> Map.entry(symbol, statistics)))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    public Optional<Statistics> getStatistics(String symbol, DateRange range) {
        return statisticsCalculator.calculate(symbol, range);
    }

}
