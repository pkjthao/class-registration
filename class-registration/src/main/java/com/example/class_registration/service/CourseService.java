package com.example.class_registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.DuplicateResourceException;
import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Course;
import com.example.class_registration.model.RegistrationStatus;
import com.example.class_registration.repository.CourseRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course createCourse(Course course) {
        if (courseRepository.findByCourseCode(course.getCourseCode()).isPresent()) {
            throw new DuplicateResourceException("Course code already exists: " + course.getCourseCode());
        }

        course.setNumEnrolled(0);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        Course existing = getCourseById(id);
        existing.setCourseName(updatedCourse.getCourseName());
        existing.setDescription(updatedCourse.getDescription());
        existing.setMaxSeats(updatedCourse.getMaxSeats());
        existing.setStartDate(updatedCourse.getStartDate());
        existing.setEndDate(updatedCourse.getEndDate());
        
        return courseRepository.save(existing);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Course not found with id: " + id));

        // Check if any students are actively enrolled
        long activeCount = course.getRegistrations().stream()
            .filter(r -> r.getStatus() == RegistrationStatus.ACTIVE)
            .count();

        if (activeCount > 0) {
            throw new RuntimeException(
                "Cannot delete a course with " + activeCount +
                " active student(s) enrolled. Drop all students first.");
        }

        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByCourseNameContainingIgnoreCase(keyword);
    }
}
