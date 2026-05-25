package com.example.betapi.service;

import com.example.betapi.model.Match;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private final Sheets sheetsService;

    @Value("${google.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.spreadsheet.range}")
    private String range;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd.M.yy HH:mm"),    // ← add this
            DateTimeFormatter.ofPattern("d.M.yy HH:mm"),     // ← and this (single digit day)
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    public List<Match> getMatches() throws IOException {
        List<Match> matches = new ArrayList<>();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            log.warn("No data found in spreadsheet");
            return matches;
        }

        // Skip header row (index 0)
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);
            try {
                Match match = parseRowToMatch(row);
                matches.add(match);
            } catch (Exception e) {
                log.error("Error parsing row {}: {}", i, e.getMessage());
            }
        }

        log.info("Successfully retrieved {} matches from Google Sheets", matches.size());
        return matches;
    }

    private Match parseRowToMatch(List<Object> row) {
        return Match.builder()
                .matchNumber(parseInteger(row, 0))
                .roundNumber(parseInteger(row, 1))
                .date(parseDateTime(row, 2))
                .location(parseString(row, 3))
                .homeTeam(parseString(row, 4))
                .awayTeam(parseString(row, 5))
                .group(parseString(row, 6))
                .result(parseString(row, 7))
                .build();
    }

    private Integer parseInteger(List<Object> row, int index) {
        if (index >= row.size()) return null;
        try {
            String value = row.get(index).toString().trim();
            return value.isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer at index {}: {}", index, row.get(index));
            return null;
        }
    }

    private String parseString(List<Object> row, int index) {
        if (index >= row.size()) return null;
        return row.get(index).toString().trim();
    }

    private LocalDateTime parseDateTime(List<Object> row, int index) {
        if (index >= row.size()) return null;
        String dateString = row.get(index).toString().trim();

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        log.warn("Failed to parse datetime at index {}: {}", index, dateString);
        return null;
    }
}

