package com.cheridanh.googlewalletservice.repositories;

import com.cheridanh.googlewalletservice.entities.StudentCard;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentCardRepository extends JpaRepository<StudentCard, Long> {

    List<StudentCard> findByStudentId(Long studentId);
    List<StudentCard> findByExpirationDateBefore(LocalDate date);

    Optional<StudentCard> findByStudentIdAndStatus(Long studentId, String status);

    @Query("SELECT sc FROM StudentCard sc WHERE sc.student.id = :studentId AND sc.status = 'ACTIVE' ORDER BY sc.createdAt DESC")
    Optional<StudentCard> findActiveCardByStudentId(@PathParam("studentId") Long studentId);
}
