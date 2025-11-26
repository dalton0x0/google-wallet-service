package com.cheridanh.googlewalletservice.mappers;

import com.cheridanh.googlewalletservice.dtos.StudentRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentResponseDto;
import com.cheridanh.googlewalletservice.dtos.StudentWithCardsDto;
import com.cheridanh.googlewalletservice.entities.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final StudentCardMapper studentCardMapper;

    public Student toStudentEntity(StudentRequestDto studentRequestDto) {
        return Student.builder()
                .firstName(studentRequestDto.getFirstName())
                .lastName(studentRequestDto.getLastName())
                .email(studentRequestDto.getEmail())
                .level(studentRequestDto.getLevel())
                .formation(studentRequestDto.getFormation())
                .profilePictureUrl(studentRequestDto.getProfilePictureUrl())
                .build();
    }

    public StudentResponseDto toStudentResponseDto(Student student) {
        return StudentResponseDto.builder()
                .studentID(student.getStudentId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .level(student.getLevel())
                .formation(student.getFormation())
                .profilePictureUrl(student.getProfilePictureUrl())
                .build();
    }

    public void updateEntityFromDto(StudentRequestDto studentRequestDto, Student existingStudent) {
        existingStudent.setFirstName(studentRequestDto.getFirstName());
        existingStudent.setLastName(studentRequestDto.getLastName());
        existingStudent.setEmail(studentRequestDto.getEmail());
        existingStudent.setLevel(studentRequestDto.getLevel());
        existingStudent.setFormation(studentRequestDto.getFormation());
    }

    public StudentWithCardsDto toStudentWithCardsDto(Student student) {
        StudentWithCardsDto dto = StudentWithCardsDto.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .level(student.getLevel())
                .formation(student.getFormation())
                .profilePictureUrl(student.getProfilePictureUrl())
                .cards(studentCardMapper.toDtoList(student.getCards()))
                .build();

        student.getActiveCard().ifPresent(activeCard ->
                dto.setActiveCard(studentCardMapper.toDto(activeCard))
        );

        return dto;
    }
}
