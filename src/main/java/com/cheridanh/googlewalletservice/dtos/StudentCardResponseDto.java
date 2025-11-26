package com.cheridanh.googlewalletservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCardResponseDto {
    private String objectId;
    private String saveUrl;
    private String message;
    private Boolean success;

    public static StudentCardResponseDto success(String objectId, String saveUrl, String message) {
        return new StudentCardResponseDto(objectId, saveUrl, message, true);
    }

    public static StudentCardResponseDto error(String message) {
        return new StudentCardResponseDto(null, null, message, false);
    }
}
