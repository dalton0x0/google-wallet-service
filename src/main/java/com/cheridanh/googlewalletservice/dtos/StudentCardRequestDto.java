package com.cheridanh.googlewalletservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentCardRequestDto {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Program is required")
    private String formation;

    @NotBlank(message = "Level is required")
    private String level;

    private String photoUrl;

    @NotNull(message = "Expiration date is required")
    private LocalDate expirationDate;
}
