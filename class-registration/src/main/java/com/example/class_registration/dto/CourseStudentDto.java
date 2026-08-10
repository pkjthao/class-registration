package com.example.class_registration.dto;

import java.time.LocalDateTime;

import com.example.class_registration.model.RegistrationStatus;

public record CourseStudentDto(
    Long registrationId,
    Long studentId,
    String firstName,
    String lastName,
    String email,
    String phone,
    LocalDateTime registrationTime,
    RegistrationStatus status
) {}