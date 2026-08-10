package com.example.class_registration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Course;
import com.example.class_registration.model.Registration;
import com.example.class_registration.model.RegistrationStatus;
import com.example.class_registration.model.Student;
import com.example.class_registration.repository.CourseRepository;
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

    @Transactional
    public Registration enrollStudent(Long studentId, Long courseId) {

        // 1. Check student exists
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Student not found with id: " + studentId));

        // 2. Check course exists
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Course not found with id: " + courseId));

        // 3. Check for an existing registration and its status
        Optional<Registration> existingReg =
            registrationRepository.findByStudentIdAndCourseId(studentId, courseId);

        if (existingReg.isPresent()) {
            RegistrationStatus status = existingReg.get().getStatus();

            if (status == RegistrationStatus.ACTIVE) {
                throw new RuntimeException("Student is already enrolled in this course");
            }

            if (status == RegistrationStatus.WAITLISTED) {
                throw new RuntimeException("Student is already on the waitlist for this course");
            }

            // Status is DROPPED — reactivate the existing registration instead of creating a duplicate row
            if (status == RegistrationStatus.DROPPED) {

                if (course.getNumEnrolled() >= course.getMaxSeats()) {
                    // Course is full — put back on waitlist
                    existingReg.get().setStatus(RegistrationStatus.WAITLISTED);
                    return registrationRepository.save(existingReg.get());
                }

                // Re-enroll by updating the existing record
                existingReg.get().setStatus(RegistrationStatus.ACTIVE);
                existingReg.get().setRegistrationTime(LocalDateTime.now());
                course.setNumEnrolled(course.getNumEnrolled() + 1);
                courseRepository.save(course);
                return registrationRepository.save(existingReg.get());
            }
        }

        // 4. No existing registration — check seat availability
        if (course.getNumEnrolled() >= course.getMaxSeats()) {
            Registration waitlisted = new Registration();
            waitlisted.setStudent(student);
            waitlisted.setCourse(course);
            waitlisted.setStatus(RegistrationStatus.WAITLISTED);
            return registrationRepository.save(waitlisted);
        }

        // 5. Fresh enrollment
        course.setNumEnrolled(course.getNumEnrolled() + 1);
        courseRepository.save(course);

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setStatus(RegistrationStatus.ACTIVE);
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration dropCourse(Long studentId, Long courseId) {
        Registration registration = registrationRepository.findByStudentIdAndCourseId(studentId, courseId).orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.ACTIVE) {
            throw new ResourceNotFoundException("Cannont drop a course that is not active");
        }

        registration.setStatus(RegistrationStatus.DROPPED);
        registrationRepository.save(registration);

        Course course = registration.getCourse();
        course.setNumEnrolled(course.getNumEnrolled() - 1);
        courseRepository.save(course);

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
}
