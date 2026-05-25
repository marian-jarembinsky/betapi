package com.example.betapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Integer matchNumber;
    private Integer roundNumber;
    private LocalDateTime date;
    private String location;
    private String homeTeam;
    private String awayTeam;
    private String group;
    private String result;
}

