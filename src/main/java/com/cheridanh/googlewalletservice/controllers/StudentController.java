package com.cheridanh.googlewalletservice.controllers;

import com.cheridanh.googlewalletservice.dtos.StudentRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentResponseDto;
import com.cheridanh.googlewalletservice.dtos.StudentWithCardsDto;
import com.cheridanh.googlewalletservice.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> allStudents = studentService.getAllStudents();
        return allStudents.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(allStudents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        try {
            StudentResponseDto student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/with-cards")
    public ResponseEntity<StudentWithCardsDto> getStudentWithCards(@PathVariable Long id) {
        try {
            StudentWithCardsDto student = studentService.getStudentWithCards(id);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<StudentResponseDto> createStudent(@Valid @RequestBody StudentRequestDto studentRequestDto) {
        try {
            StudentResponseDto studentResponseDto = studentService.createStudent(studentRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(studentResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto studentRequestDto) {
        try {
            StudentResponseDto studentResponseDto = studentService.updateStudent(id, studentRequestDto);
            return ResponseEntity.ok(studentResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
