package com.example.class_registration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class AssociationTest {

    @Test
    void courseSectionsCanHaveIndependentRostersAndGrades() {
        Course course = new Course();
        course.setCourseName("Intro to Biology");

        CourseSection section = new CourseSection();
        section.setCourse(course);
        section.setDays("Mon/Wed");
        section.setStartTime("09:00");
        section.setEndTime("10:15");

        Student student = new Student();
        Registration registration = new Registration();
        registration.setCourse(course);
        registration.setSection(section);
        registration.setStudent(student);
        registration.setGrade("A-");

        assertEquals(course, section.getCourse());
        assertEquals(section, registration.getSection());
        assertEquals("A-", registration.getGrade());
    }

    @Test
    void associationCanHaveMultipleStudentsAndInstructors() {
        Associations association = new Associations();
        association.setGroupName("Community Youth Group");
        association.setLocation("Denver");
        association.setEmail("info@example.org");
        association.setPhoneNumber("555-123-4567");

        Student student1 = new Student();
        student1.setAssociation(association);
        Student student2 = new Student();
        student2.setAssociation(association);

        Instructor instructor1 = new Instructor();
        instructor1.setAssociation(association);
        Instructor instructor2 = new Instructor();
        instructor2.setAssociation(association);

        association.setStudents(List.of(student1, student2));
        association.setInstructors(List.of(instructor1, instructor2));

        assertEquals(2, association.getStudents().size());
        assertEquals(2, association.getInstructors().size());
        assertEquals(association, student1.getAssociation());
        assertEquals(association, instructor1.getAssociation());
    }
}
