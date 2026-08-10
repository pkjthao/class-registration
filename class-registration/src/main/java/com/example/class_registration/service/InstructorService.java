package com.example.class_registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.class_registration.exception.ResourceNotFoundException;
import com.example.class_registration.model.Instructor;
import com.example.class_registration.repository.InstructorRepository;
import com.sun.jdi.request.DuplicateRequestException;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Instructor getInstructorById(Long id) {
        return instructorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

     public Instructor createInstructor(Instructor instructor) {
        if (instructorRepository.existsByEmail(instructor.getEmail())) {
            throw new DuplicateRequestException("Email already in use: " + instructor.getEmail());
        }
        instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
        return instructorRepository.save(instructor);
    }

    public Instructor updateInstructor(Long id, Instructor updatedInstructor) {
        Instructor existing = getInstructorById(id);
        existing.setFirstName(updatedInstructor.getFirstName());
        existing.setLastName(updatedInstructor.getLastName());
        existing.setEmail(updatedInstructor.getEmail());
        existing.setPhone(updatedInstructor.getPhone());
        existing.setAboutMe(updatedInstructor.getAboutMe());

        return instructorRepository.save(existing);
    }

    public void deleteInstructor(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new RuntimeException("Instructor not found with id: " + id);
        }
        instructorRepository.deleteById(id);
    }

    public Instructor findByEmailAndPassword(String email, String password) {
        Instructor instructor = instructorRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("No account found with that email"));

        if (!passwordEncoder.matches(password, instructor.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        return instructor;
    }
}