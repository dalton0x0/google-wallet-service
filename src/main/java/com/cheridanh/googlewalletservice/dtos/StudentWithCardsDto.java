package com.cheridanh.googlewalletservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithCardsDto {
    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private String level;
    private String formation;
    private String profilePictureUrl;
    private List<StudentCardDto> cards;
    private StudentCardDto activeCard;
}
