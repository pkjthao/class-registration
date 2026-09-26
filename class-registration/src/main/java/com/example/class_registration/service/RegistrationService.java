package com.example.class_registration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Course;
import com.example.class_registration.model.CourseSection;
import com.example.class_registration.model.Registration;
import com.example.class_registration.model.RegistrationStatus;
import com.example.class_registration.model.Student;
import com.example.class_registration.repository.CourseRepository;
import com.example.class_registration.repository.CourseSectionRepository;
import com.example.class_registration.repository.RegistrationRepository;
import com.example.class_registration.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    // @Transactional
    // public Registration enrollStudent(Long studentId, Long courseId) {
    //     Course course = courseRepository.findById(courseId)
    //         .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

    //     CourseSection section = course.getSections() == null || course.getSections().isEmpty()
    //         ? null
    //         : course.getSections().get(0);

    //     if (section == null) {
    //         throw new RuntimeException("This course has no sections yet. Please add a section before enrollment.");
    //     }

    //     return enrollStudent(studentId, section.getId());
    // }

    @Transactional
    public Registration enrollStudent(Long studentId, Long courseId, Long sectionId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        CourseSection section = courseSectionRepository.findById(sectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (!section.getCourse().getId().equals(course.getId())) {
            throw new RuntimeException("The selected section does not belong to this course.");
        }

        return enrollStudent(studentId, sectionId);
    }

    @Transactional
    public Registration enrollStudent(Long studentId, Long sectionId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        CourseSection section = courseSectionRepository.findById(sectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        Course course = section.getCourse();

        Optional<Registration> existingReg = registrationRepository.findByStudentIdAndSectionId(studentId, sectionId);
        if (existingReg.isPresent()) {
            RegistrationStatus status = existingReg.get().getStatus();
            if (status == RegistrationStatus.ACTIVE) {
                throw new RuntimeException("Student is already enrolled in this section");
            }
            if (status == RegistrationStatus.WAITLISTED) {
                throw new RuntimeException("Student is already on the waitlist for this section");
            }
            if (status == RegistrationStatus.DROPPED) {
                if (section.getNumEnrolled() >= section.getMaxSeats()) {
                    existingReg.get().setStatus(RegistrationStatus.WAITLISTED);
                    return registrationRepository.save(existingReg.get());
                }
                existingReg.get().setStatus(RegistrationStatus.ACTIVE);
                existingReg.get().setRegistrationTime(LocalDateTime.now());
                section.setNumEnrolled(section.getNumEnrolled() + 1);
                course.setNumEnrolled(course.getNumEnrolled() + 1);
                courseRepository.save(course);
                courseSectionRepository.save(section);
                return registrationRepository.save(existingReg.get());
            }
        }

        if (section.getNumEnrolled() >= section.getMaxSeats()) {
            Registration waitlisted = new Registration();
            waitlisted.setStudent(student);
            waitlisted.setCourse(course);
            waitlisted.setSection(section);
            waitlisted.setStatus(RegistrationStatus.WAITLISTED);
            return registrationRepository.save(waitlisted);
        }

        section.setNumEnrolled(section.getNumEnrolled() + 1);
        course.setNumEnrolled(course.getNumEnrolled() + 1);
        courseRepository.save(course);
        courseSectionRepository.save(section);

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setSection(section);
        registration.setStatus(RegistrationStatus.ACTIVE);
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration dropCourse(Long studentId, Long courseId) {
        List<Registration> registrations = registrationRepository.findByStudentId(studentId).stream()
            .filter(reg -> reg.getCourse() != null && reg.getCourse().getId().equals(courseId))
            .toList();

        if (registrations.isEmpty()) {
            throw new ResourceNotFoundException("Registration not found");
        }

        Registration registration = registrations.get(0);
        if (registration.getStatus() != RegistrationStatus.ACTIVE) {
            throw new ResourceNotFoundException("Cannot drop a course that is not active");
        }

        registration.setStatus(RegistrationStatus.DROPPED);
        registrationRepository.save(registration);

        if (registration.getSection() != null) {
            CourseSection section = registration.getSection();
            section.setNumEnrolled(Math.max(0, section.getNumEnrolled() - 1));
            courseSectionRepository.save(section);
        }

        Course course = registration.getCourse();
        if (course != null) {
            course.setNumEnrolled(Math.max(0, course.getNumEnrolled() - 1));
            courseRepository.save(course);
        }

        return registration;
    }

    public List<Registration> getStudentRegistrations(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        return registrationRepository.findByStudentId(studentId);
    }

    public List<Registration> getCourseRegistrations(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
        return registrationRepository.findByCourseId(courseId);
    }

    public List<Registration> getSectionRegistrations(Long sectionId) {
        if (!courseSectionRepository.existsById(sectionId)) {
            throw new RuntimeException("Section not found with id: " + sectionId);
        }
        return registrationRepository.findBySectionId(sectionId);
    }

    @Transactional
    public Registration updateGrade(Long registrationId, String grade) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + registrationId));
        registration.setGrade(grade == null ? null : grade.trim());
        return registrationRepository.save(registration);
    }
}
