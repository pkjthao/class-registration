package com.example.class_registration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.class_registration.model.Associations;

@Repository
public interface AssociationRepository extends JpaRepository<Associations, Long> {
    Optional<Associations> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByGroupName(String groupName);
}
