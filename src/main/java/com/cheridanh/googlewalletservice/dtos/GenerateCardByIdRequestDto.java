package com.cheridanh.googlewalletservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GenerateCardByIdRequestDto {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    private LocalDate expirationDate;
}
