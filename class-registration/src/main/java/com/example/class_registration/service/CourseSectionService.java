package com.example.class_registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Course;
import com.example.class_registration.model.CourseSection;
import com.example.class_registration.repository.CourseRepository;
import com.example.class_registration.repository.CourseSectionRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseSectionService {

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        return courseSectionRepository.findByCourseId(courseId);
    }

    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
    }

    @Transactional
    public CourseSection createSection(Long courseId, CourseSection section) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (section.getSectionName() == null || section.getSectionName().isBlank()) {
            section.setSectionName("Section " + (courseSectionRepository.findByCourseId(courseId).size() + 1));
        }

        section.setCourse(course);
        section.setNumEnrolled(0);
        if (section.getMaxSeats() == null || section.getMaxSeats() <= 0) {
            section.setMaxSeats(course.getMaxSeats());
        }
        return courseSectionRepository.save(section);
    }

    @Transactional
    public CourseSection updateSection(Long sectionId, CourseSection updatedSection) {
        CourseSection existing = getSectionById(sectionId);

        if (updatedSection.getSectionName() != null && !updatedSection.getSectionName().isBlank()) {
            existing.setSectionName(updatedSection.getSectionName().trim());
        }
        if (updatedSection.getDays() != null && !updatedSection.getDays().isBlank()) {
            existing.setDays(updatedSection.getDays().trim());
        }
        if (updatedSection.getStartTime() != null && !updatedSection.getStartTime().isBlank()) {
            existing.setStartTime(updatedSection.getStartTime().trim());
        }
        if (updatedSection.getEndTime() != null && !updatedSection.getEndTime().isBlank()) {
            existing.setEndTime(updatedSection.getEndTime().trim());
        }
        if (updatedSection.getLocation() != null) {
            existing.setLocation(updatedSection.getLocation().isBlank() ? null : updatedSection.getLocation().trim());
        }
        if (updatedSection.getMaxSeats() != null && updatedSection.getMaxSeats() > 0) {
            existing.setMaxSeats(updatedSection.getMaxSeats());
        }

        return courseSectionRepository.save(existing);
    }

    @Transactional
    public void deleteSection(Long sectionId) {
        CourseSection section = getSectionById(sectionId);
        if (section.getNumEnrolled() > 0) {
            throw new RuntimeException("Cannot delete a section that has active students enrolled.");
        }
        courseSectionRepository.delete(section);
    }
}
