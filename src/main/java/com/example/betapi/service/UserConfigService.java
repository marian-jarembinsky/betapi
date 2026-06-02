package com.example.betapi.service;

import com.example.betapi.model.UserConfig;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final Sheets sheetsService;

    @Value("${google.spreadsheet.user.config.id}")
    private String spreadsheetId;

    private static final String CONFIG_RANGE = "FifaWC2026!A2:D100";

    public Optional<UserConfig> getUserConfig(String email) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, CONFIG_RANGE)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        return values.stream()
                .filter(row -> row.size() >= 4)
                .filter(row -> email.equalsIgnoreCase(row.get(0).toString().trim()))
                .filter(row -> "TRUE".equalsIgnoreCase(row.get(3).toString().trim()))
                .map(row -> UserConfig.builder()
                        .userEmail(row.get(0).toString().trim())
                        .sheetName(row.get(1).toString().trim())
                        .role(row.get(2).toString().trim())
                        .active(true)
                        .build())
                .findFirst();
    }

    public boolean isAdmin(String email) throws IOException {
        return getUserConfig(email)
                .map(config -> "ADMIN".equalsIgnoreCase(config.getRole()))
                .orElse(false);
    }
}