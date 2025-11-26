package com.cheridanh.googlewalletservice.services;

import com.cheridanh.googlewalletservice.dtos.StudentRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentResponseDto;
import com.cheridanh.googlewalletservice.dtos.StudentWithCardsDto;
import com.cheridanh.googlewalletservice.entities.Student;
import com.cheridanh.googlewalletservice.mappers.StudentMapper;
import com.cheridanh.googlewalletservice.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentResponseDto> getAllStudents() {
        log.info("Getting all students");
        List<StudentResponseDto> allStudents = studentRepository.findAll()
                .stream()
                .map(studentMapper::toStudentResponseDto)
                .toList();
        log.info("Returning all students. Size {}", allStudents.size());
        return allStudents;
    }

    public StudentResponseDto getStudentById(Long id) {
        log.info("Getting student by id : {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id : " + id));
        return studentMapper.toStudentResponseDto(student);
    }

    public StudentWithCardsDto getStudentWithCards(Long id) {
        log.info("Getting student with cards by id : {}", id);
        Student student = studentRepository.findByIdWithCards(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id : " + id));
        return studentMapper.toStudentWithCardsDto(student);
    }

    public Student getStudentEntityById(Long id) {
        log.info("Getting student entity by id : {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id : " + id));
    }

    public StudentResponseDto createStudent(StudentRequestDto studentRequestDto) {
        log.info("Creating student {}", studentRequestDto);
        Optional<Student> studentOptional = studentRepository.findByEmail(studentRequestDto.getEmail());
        if (studentOptional.isPresent()) {
            log.error("Email is already used");
            throw new RuntimeException("Email is not available");
        }
        Student newStudent = studentMapper.toStudentEntity(studentRequestDto);
        if (studentRequestDto.getProfilePictureUrl() == null || studentRequestDto.getProfilePictureUrl().trim().isEmpty()) {
            newStudent.setProfilePictureUrl("https://avatar.iran.liara.run/username?username=" +
                    studentRequestDto.getFirstName() + "+" + studentRequestDto.getLastName());
        }
        Student savedStudent = studentRepository.save(newStudent);
        log.info("Returning saved student {}", savedStudent);
        log.info("StudentID generated : {}", savedStudent.getStudentId());
        return studentMapper.toStudentResponseDto(savedStudent);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentRequestDto) {
        log.info("Updating student with id : {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id : " + id));

        Optional<Student> studentWithEmail = studentRepository.findByEmail(studentRequestDto.getEmail());
        if (studentWithEmail.isPresent() && !studentWithEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email is already used or is not available");
        }

        studentMapper.updateEntityFromDto(studentRequestDto, existingStudent);

        if (studentRequestDto.getProfilePictureUrl() != null && !studentRequestDto.getProfilePictureUrl().trim().isEmpty()) {
            existingStudent.setProfilePictureUrl(studentRequestDto.getProfilePictureUrl());
        }

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Student updated : {}", updatedStudent);
        return studentMapper.toStudentResponseDto(updatedStudent);
    }
}
