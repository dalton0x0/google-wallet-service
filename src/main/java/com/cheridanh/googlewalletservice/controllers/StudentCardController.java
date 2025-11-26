package com.cheridanh.googlewalletservice.controllers;

import com.cheridanh.googlewalletservice.dtos.GenerateCardByIdRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardResponseDto;
import com.cheridanh.googlewalletservice.services.StudentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student-cards")
@RequiredArgsConstructor
public class StudentCardController {

    private final StudentCardService studentCardService;

    @PostMapping("/init-class")
    public ResponseEntity<Map<String, String>> initializeClass() {
        try {
            Map<String, String> response = studentCardService.initializeClass();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<StudentCardResponseDto> createStudentCard(@Valid @RequestBody StudentCardRequestDto request) {
        try {
            StudentCardResponseDto response = studentCardService.createStudentCard(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            StudentCardResponseDto errorResponse = StudentCardResponseDto.error(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/generate-by-id")
    public ResponseEntity<StudentCardResponseDto> generateCardByStudentId(
            @Valid @RequestBody GenerateCardByIdRequestDto request) {
        try {
            StudentCardResponseDto response = studentCardService.generateCardByStudentId(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            StudentCardResponseDto errorResponse = StudentCardResponseDto.error(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update-by-id")
    public ResponseEntity<StudentCardResponseDto> updateCardByStudentId(
            @Valid @RequestBody GenerateCardByIdRequestDto request) {
        try {
            StudentCardResponseDto response = studentCardService.updateCardByStudentId(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            StudentCardResponseDto errorResponse = StudentCardResponseDto.error(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentCardDto>> getCardsByStudentId(@PathVariable Long studentId) {
        try {
            List<StudentCardDto> cards = studentCardService.getCardsByStudentId(studentId);
            return ResponseEntity.ok(cards);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<StudentCardDto> getActiveCardByStudentId(@PathVariable Long studentId) {
        try {
            StudentCardDto card = studentCardService.getActiveCardByStudentId(studentId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<StudentCardDto> getCardById(@PathVariable Long cardId) {
        try {
            StudentCardDto card = studentCardService.getCardById(cardId);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{cardId}/revoke")
    public ResponseEntity<Map<String, String>> revokeCard(@PathVariable Long cardId) {
        try {
            studentCardService.revokeCard(cardId);
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Carte révoquée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/expire-old-cards")
    public ResponseEntity<Map<String, String>> expireOldCards() {
        try {
            studentCardService.expireOldCards();
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Cartes expirées mises à jour");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
