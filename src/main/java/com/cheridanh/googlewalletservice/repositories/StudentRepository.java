package com.cheridanh.googlewalletservice.repositories;

import com.cheridanh.googlewalletservice.entities.Student;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.cards WHERE s.id = :id")
    Optional<Student> findByIdWithCards(@PathParam("id") Long id);
}
