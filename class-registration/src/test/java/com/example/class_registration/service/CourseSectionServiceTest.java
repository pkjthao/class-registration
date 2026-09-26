package com.example.class_registration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.class_registration.model.Course;
import com.example.class_registration.model.CourseSection;
import com.example.class_registration.repository.CourseSectionRepository;

@ExtendWith(MockitoExtension.class)
class CourseSectionServiceTest {

    @Mock
    private CourseSectionRepository courseSectionRepository;

    @InjectMocks
    private CourseSectionService courseSectionService;

    @Test
    void updateSectionUpdatesSectionDetails() {
        Course course = new Course();
        course.setId(10L);
        course.setMaxSeats(30);

        CourseSection existing = new CourseSection();
        existing.setId(5L);
        existing.setCourse(course);
        existing.setSectionName("Section A");
        existing.setDays("Mon/Wed");
        existing.setStartTime("09:00");
        existing.setEndTime("10:15");
        existing.setLocation("Room 101");
        existing.setMaxSeats(30);
        existing.setNumEnrolled(0);

        CourseSection updated = new CourseSection();
        updated.setSectionName("Section B");
        updated.setDays("Tue/Thu");
        updated.setStartTime("13:00");
        updated.setEndTime("14:30");
        updated.setLocation("Lab 2");
        updated.setMaxSeats(25);

        when(courseSectionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(courseSectionRepository.save(existing)).thenReturn(existing);

        CourseSection result = courseSectionService.updateSection(5L, updated);

        assertEquals("Section B", result.getSectionName());
        assertEquals("Tue/Thu", result.getDays());
        assertEquals("13:00", result.getStartTime());
        assertEquals("14:30", result.getEndTime());
        assertEquals("Lab 2", result.getLocation());
        assertEquals(25, result.getMaxSeats());
        verify(courseSectionRepository).save(existing);
    }
}
