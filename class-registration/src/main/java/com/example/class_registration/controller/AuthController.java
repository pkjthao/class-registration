package com.example.class_registration.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.class_registration.model.Instructor;
import com.example.class_registration.model.Student;
import com.example.class_registration.repository.StudentRepository;
import com.example.class_registration.service.AssociationService;
import com.example.class_registration.service.InstructorService;
import com.example.class_registration.service.StudentService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private AssociationService associationService;

    @Autowired StudentRepository studentRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/student/login";
    }

    @GetMapping("/student/register")
    public String studentRegisterPage(Model model) {
        model.addAttribute("associations", associationService.getAllAssociations());
        return "student-register";
    }

    @GetMapping("/instructor/register")
    public String instructorRegisterPage(Model model) {
        model.addAttribute("associations", associationService.getAllAssociations());
        return "instructor-register";
    }

    @GetMapping("/student/login")
    public String studentLoginPage() {
        return "student-login";
    }

    @GetMapping("/instructor/login")
    public String instructorLoginPage() {
        return "instructor-login";
    }

    @PostMapping("/student/register")
    public String registerStudent(@ModelAttribute Student student,
                                @RequestParam Long associationId,
                                Model model) {
        try {
            student.setAssociation(associationService.getAssociationById(associationId));
            studentService.createStudent(student);
            return "redirect:/student/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("associations", associationService.getAllAssociations());
            return "student-register";
        }
    }

    @PostMapping("/instructor/register")
    public String registerInstructor(@ModelAttribute Instructor instructor,
                                    @RequestParam Long associationId,
                                    Model model) {
        try {
            instructor.setAssociation(associationService.getAssociationById(associationId));
            instructorService.createInstructor(instructor);
            return "redirect:/instructor/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("associations", associationService.getAllAssociations());
            return "instructor-register";
        }
    }

    @PostMapping("/student/login")
    public String loginStudent(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            Student student = studentService.findByEmailAndPassword(email, password);

            // Create Spring Security authentication token with ROLE_STUDENT
            var auth = new UsernamePasswordAuthenticationToken(
                student.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );

            SecurityContext sc = SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(auth);
            SecurityContextHolder.setContext(sc);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            // Also store name for display
            session.setAttribute("studentName", student.getFirstName());
            session.setAttribute("studentId", student.getId());
            session.setAttribute("role", "STUDENT");
            return "redirect:/student/home";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "student-login";
        }
    }

    @GetMapping("/student/verify")
    public String verifyStudent(@RequestParam String token, Model model) {
        Student student = studentRepository.findByVerificationToken(token)
            .orElse(null);

        if (student == null) {
            model.addAttribute("error", "Invalid or expired verification link.");
            return "error";
        }

        if (student.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Verification link has expired.");
            return "error";
        }

        student.setEnabled(true);
        student.setVerificationToken(null);
        student.setVerificationTokenExpiry(null);
        studentRepository.save(student);

        return "redirect:/student/login?verified=true";
    }

    @GetMapping("/instructor/verify")
    public String verifyInstructor(@RequestParam String token, Model model) {
        Instructor instructor = instructorService.getAllInstructors().stream()
            .filter(i -> token.equals(i.getVerificationToken()))
            .findFirst()
            .orElse(null);

        if (instructor == null) {
            model.addAttribute("error", "Invalid or expired verification link.");
            return "error";
        }

        if (instructor.getVerificationTokenExpiry() == null ||
            instructor.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Verification link has expired.");
            return "error";
        }

        instructor.setEnabled(true);
        instructor.setVerificationToken(null);
        instructor.setVerificationTokenExpiry(null);
        instructorService.updateInstructor(instructor.getId(), instructor);

        return "redirect:/instructor/login?verified=true";
    }

    @PostMapping("/instructor/login")
    public String loginInstructor(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        try {
            Instructor instructor = instructorService.findByEmailAndPassword(email, password);

            var auth = new UsernamePasswordAuthenticationToken(
                instructor.getId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))
            );

            SecurityContext sc = SecurityContextHolder.createEmptyContext();
            sc.setAuthentication(auth);
            SecurityContextHolder.setContext(sc);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            session.setAttribute("instructorName", instructor.getFirstName());
            session.setAttribute("instructorId", instructor.getId());
            session.setAttribute("role", "INSTRUCTOR");
            return "redirect:/instructor/home";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "instructor-login";
        }
    }
}