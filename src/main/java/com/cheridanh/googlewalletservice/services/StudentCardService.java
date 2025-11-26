package com.cheridanh.googlewalletservice.services;

import com.cheridanh.googlewalletservice.dtos.GenerateCardByIdRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardRequestDto;
import com.cheridanh.googlewalletservice.dtos.StudentCardResponseDto;
import com.cheridanh.googlewalletservice.entities.Student;
import com.cheridanh.googlewalletservice.entities.StudentCard;
import com.cheridanh.googlewalletservice.mappers.StudentCardMapper;
import com.cheridanh.googlewalletservice.repositories.StudentCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentCardService {

    private final GoogleWalletService googleWalletService;
    private final StudentService studentService;
    private final StudentCardRepository studentCardRepository;
    private final StudentCardMapper studentCardMapper;

    @Value("${google.wallet.issuer.id}")
    private String issuerId;

    public Map<String, String> initializeClass() {
        log.info("Initializing Google Wallet class");
        try {
            String classId = googleWalletService.createOrUpdateGenericClass();
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("classId", classId);
            response.put("message", "Classe créée avec succès");
            log.info("Class initialized successfully : {}", classId);
            return response;
        } catch (IOException e) {
            log.error("Error initializing class : {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            throw new RuntimeException("Failed to initialize class : " + e.getMessage(), e);
        }
    }

    public StudentCardResponseDto createStudentCard(StudentCardRequestDto request) {
        log.info("Creating student card for : {}", request.getStudentId());
        try {
            String saveUrl = googleWalletService.createStudentCard(request);
            String objectId = generateObjectId(request.getStudentId());

            log.info("Student card created successfully : {}", objectId);
            return StudentCardResponseDto.success(objectId, saveUrl, "Carte étudiante créée avec succès");

        } catch (IOException e) {
            log.error("Error creating student card : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de la carte : " + e.getMessage(), e);
        }
    }

    public StudentCardResponseDto generateCardByStudentId(GenerateCardByIdRequestDto request) {
        log.info("Generating card for student ID : {}", request.getStudentId());
        try {
            Student student = studentService.getStudentEntityById(request.getStudentId());

            deactivatePreviousCards(student.getId());

            StudentCardRequestDto cardRequest = convertToCardRequest(student, request.getExpirationDate());

            String saveUrl = googleWalletService.createStudentCard(cardRequest);
            String objectId = generateObjectId(cardRequest.getStudentId());

            StudentCard studentCard = createStudentCardEntity(student, cardRequest, objectId, saveUrl);
            studentCardRepository.save(studentCard);

            log.info("Card generated and saved successfully for student ID : {}", request.getStudentId());
            return StudentCardResponseDto.success(
                    objectId, saveUrl, "Carte étudiante générée avec succès");

        } catch (RuntimeException e) {
            log.error("Runtime error generating card : {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error("IO error generating card : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération de la carte : " + e.getMessage(), e);
        }
    }

    public StudentCardResponseDto updateCardByStudentId(GenerateCardByIdRequestDto request) {
        log.info("Updating card for student ID : {}", request.getStudentId());
        try {
            Student student = studentService.getStudentEntityById(request.getStudentId());

            Optional<StudentCard> existingCardOpt = studentCardRepository.findActiveCardByStudentId(student.getId());

            StudentCardRequestDto cardRequest = convertToCardRequest(student, request.getExpirationDate());

            String saveUrl = googleWalletService.createStudentCard(cardRequest);
            String objectId = generateObjectId(cardRequest.getStudentId());

            StudentCard studentCard;
            if (existingCardOpt.isPresent()) {
                studentCard = existingCardOpt.get();
                updateStudentCardEntity(studentCard, cardRequest, objectId, saveUrl);
                log.info("Updating existing card in database");
            } else {
                studentCard = createStudentCardEntity(student, cardRequest, objectId, saveUrl);
                log.info("Creating new card in database");
            }

            studentCardRepository.save(studentCard);

            log.info("Card updated successfully for student ID : {}", request.getStudentId());
            return StudentCardResponseDto.success(
                    objectId, saveUrl, "Carte étudiante mise à jour avec succès");

        } catch (RuntimeException e) {
            log.error("Runtime error updating card : {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error("IO error updating card : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de la carte : " + e.getMessage(), e);
        }
    }

    public List<StudentCardDto> getCardsByStudentId(Long studentId) {
        log.info("Getting all cards for student ID : {}", studentId);
        List<StudentCard> cards = studentCardRepository.findByStudentId(studentId);
        return studentCardMapper.toDtoList(cards);
    }

    public StudentCardDto getActiveCardByStudentId(Long studentId) {
        log.info("Getting active card for student ID : {}", studentId);
        StudentCard card = studentCardRepository.findActiveCardByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("No active card found for student ID : " + studentId));
        return studentCardMapper.toDto(card);
    }

    public StudentCardDto getCardById(Long cardId) {
        log.info("Getting card by ID : {}", cardId);
        StudentCard card = studentCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID : " + cardId));
        return studentCardMapper.toDto(card);
    }

    public void revokeCard(Long cardId) {
        log.info("Revoking card ID : {}", cardId);
        StudentCard card = studentCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID : " + cardId));
        card.setStatus("REVOKED");
        studentCardRepository.save(card);
        log.info("Card revoked successfully : {}", cardId);
    }

    public void expireOldCards() {
        log.info("Expiring old cards");
        LocalDate today = LocalDate.now();
        List<StudentCard> expiredCards = studentCardRepository.findByExpirationDateBefore(today);

        expiredCards.forEach(card -> {
            if ("ACTIVE".equals(card.getStatus())) {
                card.setStatus("EXPIRED");
                log.info("Card expired : {}", card.getWalletObjectId());
            }
        });

        studentCardRepository.saveAll(expiredCards);
        log.info("Expired {} cards", expiredCards.size());
    }

    private void deactivatePreviousCards(Long studentId) {
        log.info("Deactivating previous cards for student ID : {}", studentId);
        List<StudentCard> activeCards = studentCardRepository
                .findByStudentIdAndStatus(studentId, "ACTIVE")
                .stream()
                .toList();

        activeCards.forEach(card -> card.setStatus("REPLACED"));
        studentCardRepository.saveAll(activeCards);
        log.info("Deactivated {} previous cards : ", activeCards.size());
    }

    private StudentCard createStudentCardEntity(Student student, StudentCardRequestDto cardRequest, String objectId, String saveUrl) {
        return StudentCard.builder()
                .walletObjectId(objectId)
                .saveUrl(saveUrl)
                .expirationDate(cardRequest.getExpirationDate())
                .status("ACTIVE")
                .level(cardRequest.getLevel())
                .formation(cardRequest.getFormation())
                .academicYear(determineAcademicYear(cardRequest.getExpirationDate()))
                .photoUrl(cardRequest.getPhotoUrl())
                .student(student)
                .build();
    }

    private void updateStudentCardEntity(StudentCard card, StudentCardRequestDto cardRequest, String objectId, String saveUrl) {
        card.setWalletObjectId(objectId);
        card.setSaveUrl(saveUrl);
        card.setExpirationDate(cardRequest.getExpirationDate());
        card.setLevel(cardRequest.getLevel());
        card.setFormation(cardRequest.getFormation());
        card.setAcademicYear(determineAcademicYear(cardRequest.getExpirationDate()));
        card.setPhotoUrl(cardRequest.getPhotoUrl());
        card.setStatus("ACTIVE");
    }

    private StudentCardRequestDto convertToCardRequest(Student student, LocalDate expirationDate) {
        log.debug("Converting student to card request : {}", student.getStudentId());
        StudentCardRequestDto cardRequest = new StudentCardRequestDto();
        cardRequest.setStudentId(student.getStudentId());
        cardRequest.setFirstName(student.getFirstName());
        cardRequest.setLastName(student.getLastName());
        cardRequest.setEmail(student.getEmail());
        cardRequest.setFormation(student.getFormation());
        cardRequest.setLevel(student.getLevel());
        cardRequest.setPhotoUrl(student.getProfilePictureUrl());
        cardRequest.setExpirationDate(expirationDate != null ? expirationDate : LocalDate.now().plusYears(1));
        return cardRequest;
    }

    private String generateObjectId(String studentId) {
        return String.format("%s.%s", issuerId,
                studentId.toLowerCase().replaceAll("[^a-z0-9]", "_"));
    }

    private String determineAcademicYear(LocalDate expirationDate) {
        if (expirationDate == null) {
            return LocalDate.now().getYear() + " - " + (LocalDate.now().getYear() + 1);
        }
        int year = expirationDate.getYear();
        return (year - 1) + " - " + year;
    }
}
