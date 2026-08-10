package com.example.class_registration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.class_registration.model.Registration;
import com.example.class_registration.model.RegistrationStatus;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long>{

    List<Registration> findByStudentId(Long studentId);

    List<Registration> findByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Registration> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Registration> findByStatus(RegistrationStatus status);
}
