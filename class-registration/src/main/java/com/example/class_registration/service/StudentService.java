package com.example.class_registration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.DuplicateResourceException;
import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Student;
import com.example.class_registration.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
    return studentRepository.findById(id)
        .orElseThrow(() ->
            new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException(
                "An account already exists with email: " + student.getEmail());
        }

        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setEnabled(false);
        student.setVerificationToken(UUID.randomUUID().toString());
        student.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        Student saved = studentRepository.save(student);

        String verifyUrl = "http://localhost:8080/student/verify?token="
                + saved.getVerificationToken();

        try {
            emailService.sendSingleEmail(
                saved.getEmail(),
                "Verify your account",
                "<p>Hello " + saved.getFirstName() + ",</p>"
                + "<p>Click <a href='" + verifyUrl + "'>here</a> to verify your account.</p>"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email to " + saved.getEmail(), e);
        }

        return saved;
    }

    public Student updateStudent(Long id, Student updateStudent) {
        Student existing = getStudentById(id);
        existing.setFirstName(updateStudent.getFirstName());
        existing.setMiddleName(updateStudent.getMiddleName());
        existing.setLastName(updateStudent.getLastName());
        existing.setEmail(updateStudent.getEmail());
        existing.setPhone(updateStudent.getPhone());
        
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }

        studentRepository.deleteById(id);
    }

    public Student findByEmailAndPassword(String email, String password) {
        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new ResourceNotFoundException("No account found with that email"));

        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        return student;
    }
}
