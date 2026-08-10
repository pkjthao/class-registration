package com.example.class_registration.service;

import java.util.List;

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
        return studentRepository.save(student);
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
