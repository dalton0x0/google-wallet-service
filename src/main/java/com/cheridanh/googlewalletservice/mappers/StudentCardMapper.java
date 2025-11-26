package com.cheridanh.googlewalletservice.mappers;

import com.cheridanh.googlewalletservice.dtos.StudentCardDto;
import com.cheridanh.googlewalletservice.entities.StudentCard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentCardMapper {

    public StudentCardDto toDto(StudentCard studentCard) {
        return StudentCardDto.builder()
                .id(studentCard.getId())
                .walletObjectId(studentCard.getWalletObjectId())
                .saveUrl(studentCard.getSaveUrl())
                .expirationDate(studentCard.getExpirationDate())
                .status(studentCard.getStatus())
                .level(studentCard.getLevel())
                .formation(studentCard.getFormation())
                .academicYear(studentCard.getAcademicYear())
                .photoUrl(studentCard.getPhotoUrl())
                .createdAt(studentCard.getCreatedAt())
                .updatedAt(studentCard.getUpdatedAt())
                .isExpired(studentCard.isExpired())
                .isActive(studentCard.isActive())
                .build();
    }

    public List<StudentCardDto> toDtoList(List<StudentCard> studentCards) {
        return studentCards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
