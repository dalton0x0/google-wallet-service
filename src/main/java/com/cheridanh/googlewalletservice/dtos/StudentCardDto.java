package com.cheridanh.googlewalletservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCardDto {
    private Long id;
    private String walletObjectId;
    private String saveUrl;
    private LocalDate expirationDate;
    private String status;
    private String level;
    private String formation;
    private String academicYear;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isExpired;
    private boolean isActive;
}
