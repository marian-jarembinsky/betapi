package com.example.betapi.controller;

import com.example.betapi.model.Match;
import com.example.betapi.service.GoogleSheetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MatchController {

    private final GoogleSheetsService googleSheetsService;

    @GetMapping("/matches")
    public ResponseEntity<List<Match>> getMatches(@AuthenticationPrincipal Jwt jwt) {
        try {
            log.info("Fetching matches for user: {}", jwt.getSubject());
            List<Match> matches = googleSheetsService.getMatches();
            return ResponseEntity.ok(matches);
        } catch (IOException e) {
            log.error("Error fetching matches from Google Sheets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/matches/{matchNumber}")
    public ResponseEntity<Match> getMatchByNumber(
            @PathVariable Integer matchNumber,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            log.info("Fetching match {} for user: {}", matchNumber, jwt.getSubject());
            List<Match> matches = googleSheetsService.getMatches();
            return matches.stream()
                    .filter(match -> match.getMatchNumber() != null &&
                            match.getMatchNumber().equals(matchNumber))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            log.error("Error fetching match from Google Sheets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/matches/round/{roundNumber}")
    public ResponseEntity<List<Match>> getMatchesByRound(
            @PathVariable Integer roundNumber,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            log.info("Fetching matches for round {} for user: {}", roundNumber, jwt.getSubject());
            List<Match> matches = googleSheetsService.getMatches();
            List<Match> filteredMatches = matches.stream()
                    .filter(match -> match.getRoundNumber() != null &&
                            match.getRoundNumber().equals(roundNumber))
                    .toList();
            return ResponseEntity.ok(filteredMatches);
        } catch (IOException e) {
            log.error("Error fetching matches from Google Sheets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("BetAPI is running");
    }
}

