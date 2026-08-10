package com.example.class_registration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.class_registration.model.Instructor;
import com.example.class_registration.model.Student;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long>{

    Optional<Instructor> findByLastName(String lastName);

    boolean existsByEmail(String email);

    Optional<Instructor> findByEmail(String email);

    Optional<Instructor> findByEmailAndPassword(String email, String password);
}
