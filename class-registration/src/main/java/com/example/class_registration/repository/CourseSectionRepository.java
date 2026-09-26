package com.example.class_registration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.class_registration.model.CourseSection;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseId(Long courseId);
    Optional<CourseSection> findByCourseIdAndSectionName(Long courseId, String sectionName);
}
