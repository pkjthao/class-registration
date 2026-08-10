package com.example.class_registration.controller;

import com.example.class_registration.model.Registration;
import com.example.class_registration.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentHomeController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/home")
    public String studentHome(HttpSession session, Model model) {

        // Redirect to login if not logged in
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/student/login";
        }

        List<Registration> registrations =
            registrationService.getStudentRegistrations(studentId);

        model.addAttribute("studentName", session.getAttribute("studentName"));
        model.addAttribute("registrations", registrations);
        return "student-home";
    }

    @PostMapping("/drop")
    public String dropCourse(@RequestParam Long courseId,
                             HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";

        registrationService.dropCourse(studentId, courseId);
        return "redirect:/student/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/student/login";
    }
}