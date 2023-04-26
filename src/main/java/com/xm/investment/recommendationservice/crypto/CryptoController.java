package com.xm.investment.recommendationservice.crypto;

import com.xm.investment.recommendationservice.crypto.model.DateRange;
import com.xm.investment.recommendationservice.crypto.model.Statistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Operation(summary = "Get crypto recommendation list sorted by normalized range statistics.")
    @ApiResponse(responseCode = "200", description = "Found crypto recommendation",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content)
    @GetMapping("/cryptos")
    @Parameter(in = ParameterIn.QUERY, name = "date", description = "Specific day for which statistics is calculated")
    @Parameter(in = ParameterIn.QUERY, name = "from", description = "Day from which statistics is calculated")
    List<String> cryptos(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalDate from) {
        DateRange dateRange = parseDateRange(date, from);
        Map<String, Statistics> cryptos = cryptoService.getStatistics(dateRange);
        return cryptos.entrySet().stream()
                .sorted(Comparator
                        .comparing(entry -> entry.getValue().normalizedRange()))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Operation(summary = "Get basic statistics for selected crypto")
    @ApiResponse(responseCode = "200", description = "Found crypto recommendation",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content)
    @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content)
    @Parameter(in = ParameterIn.QUERY, name = "date", description = "Specific day for which statistics is calculated")
    @Parameter(in = ParameterIn.QUERY, name = "from", description = "Day from which statistics is calculated")
    @GetMapping("/cryptos/{symbol}")
    ResponseEntity<BasicStatistics> cryptoBasicStatistics(
            @PathVariable String symbol,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalDate from) {
        DateRange dateRange = parseDateRange(date, from);
        return cryptoService.getStatistics(symbol, dateRange)
                .map(BasicStatistics::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private record BasicStatistics(Instant oldest,
                                   Instant newest,
                                   BigDecimal min,
                                   BigDecimal max) {
        static BasicStatistics from(Statistics statistics) {
            return new BasicStatistics(
                    statistics.oldest().timestamp(),
                    statistics.newest().timestamp(),
                    statistics.min().price(),
                    statistics.max().price());
        }
    }

    private static DateRange parseDateRange(LocalDate date, LocalDate from) {
        Instant fromInst;
        Instant toInst;
        if (date != null) {
            fromInst = Instant.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());
            toInst = fromInst.plus(1, ChronoUnit.DAYS);
            return new DateRange(fromInst, toInst);
        } else if (from != null) {
            fromInst = Instant.from(from.atStartOfDay(ZoneOffset.UTC).toInstant());
            toInst = Instant.MAX;
            return new DateRange(fromInst, toInst);
        } else {
            return DateRange.UNLIMITED;
        }
    }
}
