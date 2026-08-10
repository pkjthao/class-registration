package com.example.class_registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.class_registration.model.Course;
import com.example.class_registration.service.CourseService;
import com.example.class_registration.service.RegistrationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<List<Course>> getAvailableCourses() {
        return ResponseEntity.ok(courseService.getAvailableCourses());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(courseService.createCourse(course));
    }

    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @GetMapping("/display")
    public String classDisplay(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String instructor,
            Model model) {

        List<Course> courses;
        if (search != null && !search.isEmpty()) {
            courses = courseService.searchCourses(search);
        } else {
            courses = courseService.getAllCourses();
        }

        model.addAttribute("courses", courses);
        model.addAttribute("search", search);
        return "class-display";
    }

    @GetMapping("/{id}/info")
    public String classInfo(@PathVariable Long id,
                            HttpSession session,
                            Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("studentId", session.getAttribute("studentId"));
        return "class-info";
    }

    @GetMapping("/{id}/register")
    public String registerPage(@PathVariable Long id,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "register-class";
    }

    @PostMapping("/{id}/register")
    public String registerForCourse(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";

        try {
            registrationService.enrollStudent(studentId, id);
            return "redirect:/student/home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("course", courseService.getCourseById(id));
            return "register-class";
        }
    }

    @PostMapping("/waitlist")
    public String joingWaitlist(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        Course course = courseService.getCourseById((Long)session.getAttribute("studentId"));

        if (course.getMaxSeats().equals(course.getNumEnrolled())) {
             try {
                registrationService.enrollStudent(studentId, id);
                return "redirect:/student/home";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("course", courseService.getCourseById(id));
                return "register-class";
            }
        }

        return "register-class";
    }
}
