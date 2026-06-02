package com.example.betapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {
    private String userEmail;
    private String sheetName;
    private String role;
    private boolean active;
}