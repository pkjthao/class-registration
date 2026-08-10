package com.example.class_registration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.class_registration.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByInstructorId(Long instructorId);

    @Query("SELECT c FROM Course c WHERE c.numEnrolled < c.maxSeats")
    List<Course> findAvailableCourses();

    List<Course> findByCourseNameContainingIgnoreCase(String keyword);
}
