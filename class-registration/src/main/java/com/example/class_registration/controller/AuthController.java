package com.example.class_registration.controller;

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
import com.example.class_registration.service.InstructorService;
import com.example.class_registration.service.StudentService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private InstructorService instructorService;

    @GetMapping("/")
    public String root() {
        return "redirect:/student/login";
    }

    @GetMapping("/student/register")
    public String studentRegisterPage() {
        return "student-register";   
    }

    @GetMapping("/instructor/register")
    public String instructorRegisterPage() {
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
    public String registerStudent(@ModelAttribute Student student, Model model) {
        try {
            studentService.createStudent(student);
            return "redirect:/student/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "student-register";
        }
    }

    @PostMapping("/instructor/register")
    public String registerInstructor(@ModelAttribute Instructor instructor, Model model) {
        try {
            instructorService.createInstructor(instructor);
            return "redirect:/instructor/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
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