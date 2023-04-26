package com.xm.investment.recommendationservice.crypto.repository;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class CryptoCsvFileReader {

    private static final ObjectReader objectReader = new CsvMapper()
            .readerFor(CryptoCsvRow.class)
            .with(CsvSchema.emptySchema().withHeader());

    static List<CryptoCsvRow> readCryptoCsvRows(Path path) {
        try (MappingIterator<CryptoCsvRow> rows = objectReader.readValues(path.toFile())) {
            return rows.readAll();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
