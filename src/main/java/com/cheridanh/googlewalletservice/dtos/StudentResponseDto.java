package com.cheridanh.googlewalletservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponseDto {
    private String studentID;
    private String fullName;
    private String email;
    private String level;
    private String formation;
    private String profilePictureUrl;
}
