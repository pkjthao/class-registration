package com.example.class_registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.class_registration.model.Registration;
import com.example.class_registration.service.RegistrationService;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/enroll")
    public ResponseEntity<Registration> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(registrationService.enrollStudent(studentId, courseId));
    }

    @PutMapping("/drop")
    public ResponseEntity<Registration> dropCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(registrationService.dropCourse(studentId, courseId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Registration>> getStudentRegistrations(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(registrationService.getStudentRegistrations(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Registration>> getCourseRegistrations(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(registrationService.getCourseRegistrations(courseId));
    }
}