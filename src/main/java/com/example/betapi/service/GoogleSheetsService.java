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
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private final Sheets sheetsService;

    @Value("${google.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.spreadsheet.fifa2026.id}")
    private String spreadSheetFifa26Id;

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
                .get(spreadSheetFifa26Id, range)
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

    /**
     * Writes a user's result bet into their personal sheet.
     *
     * User sheet structure:
     *   A1 = "Result" (header)
     *   A2 = result for match 1
     *   A3 = result for match 2
     *   A{matchNumber+1} = result for match N
     *
     * @param sheetName  the tab name of the user's personal sheet (from UserConfig)
     * @param matchNumber the match number to write the result for
     * @param result      the result string (e.g. "2:1")
     */
    public void updateResult(String sheetName, Integer matchNumber, String result) throws IOException {
        // A1 is header "Result", so match 1 → A2, match 2 → A3, etc.
        int rowNumber = matchNumber + 1;
        String cellRange = sheetName + "!H" + rowNumber;

        ValueRange body = new ValueRange()
                .setValues(Collections.singletonList(
                        Collections.singletonList(result)
                ));

        sheetsService.spreadsheets().values()
                .update(spreadSheetFifa26Id, cellRange, body)
                .setValueInputOption("RAW")
                .execute();

        log.info("Updated result for match {} to '{}' in sheet '{}' at cell A{}", matchNumber, result, sheetName, rowNumber);
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

