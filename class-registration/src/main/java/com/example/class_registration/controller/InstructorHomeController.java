package com.example.class_registration.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.class_registration.dto.CourseStudentDto;
import com.example.class_registration.model.Course;
import com.example.class_registration.model.CourseSection;
import com.example.class_registration.model.Instructor;
import com.example.class_registration.model.Registration;
import com.example.class_registration.service.CourseSectionService;
import com.example.class_registration.service.CourseService;
import com.example.class_registration.service.EmailService;
import com.example.class_registration.service.InstructorService;
import com.example.class_registration.service.RegistrationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/instructor")
public class InstructorHomeController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CourseSectionService courseSectionService;

    @GetMapping("/home")
    public String instructorHome(HttpSession session, Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        List<Course> courses = courseService.getCoursesByInstructorId(instructorId);
        model.addAttribute("instructorName", session.getAttribute("instructorName"));
        model.addAttribute("courses", courses);
        return "instructor-home";
    }

    // Returns student list for a course as JSON for printing
    @GetMapping("/course/{id}/students")
    @ResponseBody
    public List<CourseStudentDto> getCourseStudents(@PathVariable Long id,
                                                    HttpSession session) {
        if (session.getAttribute("instructorId") == null) return List.of();

        return registrationService.getCourseRegistrations(id).stream()
            .map(r -> new CourseStudentDto(
                r.getRegistrationId(),
                r.getStudent().getId(),
                r.getStudent().getFirstName(),
                r.getStudent().getLastName(),
                r.getStudent().getEmail(),
                r.getStudent().getPhone(),
                r.getRegistrationTime(),
                r.getStatus()
            ))
            .toList();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/instructor/login";
    }

    @GetMapping("/course/{id}/edit")
    public String editCoursePage(@PathVariable Long id,
                                @RequestParam(required = false) Long sectionId,
                                HttpSession session,
                                Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course course = courseService.getCourseById(id);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        List<CourseSection> sections = courseSectionService.getSectionsByCourseId(id);
        CourseSection selectedSection = null;
        if (sections != null && !sections.isEmpty()) {
            if (sectionId != null) {
                selectedSection = sections.stream()
                    .filter(s -> s.getId().equals(sectionId))
                    .findFirst()
                    .orElse(sections.get(0));
            } else {
                selectedSection = sections.get(0);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("sections", sections);
        model.addAttribute("selectedSection", selectedSection);
        model.addAttribute("selectedSectionId", selectedSection != null ? selectedSection.getId() : null);
        return "edit-course";
    }

    @PostMapping("/course/{id}/section/add")
    public String addSection(@PathVariable Long id,
                             @RequestParam String sectionName,
                             @RequestParam String days,
                             @RequestParam String startTime,
                             @RequestParam String endTime,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false, defaultValue = "0") Integer maxSeats,
                             HttpSession session,
                             Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course course = courseService.getCourseById(id);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        try {
            CourseSection section = new CourseSection();
            section.setSectionName(sectionName == null || sectionName.isBlank() ? "Section " + (course.getSections().size() + 1) : sectionName.trim());
            section.setDays(days);
            section.setStartTime(startTime);
            section.setEndTime(endTime);
            section.setLocation(location);
            section.setMaxSeats(maxSeats != null && maxSeats > 0 ? maxSeats : course.getMaxSeats());
            courseSectionService.createSection(id, section);
            return "redirect:/instructor/course/" + id + "/edit?sectionAdded=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("course", course);
            model.addAttribute("sections", courseSectionService.getSectionsByCourseId(id));
            return "edit-course";
        }
    }

    @PostMapping("/course/{courseId}/section/{sectionId}/update")
    public String updateSection(@PathVariable Long courseId,
                               @PathVariable Long sectionId,
                               @RequestParam String sectionName,
                               @RequestParam String days,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               @RequestParam(required = false) String location,
                               @RequestParam(required = false) Integer maxSeats,
                               HttpSession session,
                               Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course course = courseService.getCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        try {
            CourseSection section = new CourseSection();
            section.setSectionName(sectionName);
            section.setDays(days);
            section.setStartTime(startTime);
            section.setEndTime(endTime);
            section.setLocation(location);
            section.setMaxSeats(maxSeats != null && maxSeats > 0 ? maxSeats : course.getMaxSeats());
            courseSectionService.updateSection(sectionId, section);
            return "redirect:/instructor/course/" + courseId + "/edit?sectionUpdated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("course", course);
            model.addAttribute("sections", courseSectionService.getSectionsByCourseId(courseId));
            return "edit-course";
        }
    }

    @PostMapping("/course/{courseId}/section/{sectionId}/delete")
    public String deleteSection(@PathVariable Long courseId,
                               @PathVariable Long sectionId,
                               HttpSession session) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course course = courseService.getCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        courseSectionService.deleteSection(sectionId);
        return "redirect:/instructor/course/" + courseId + "/edit?sectionRemoved=true";
    }

    @PostMapping("/course/{courseId}/section/{sectionId}/grade")
    public String updateSectionGrade(@PathVariable Long courseId,
                                    @PathVariable Long sectionId,
                                    @RequestParam Long registrationId,
                                    @RequestParam(required = false) String grade,
                                    HttpSession session) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course course = courseService.getCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        registrationService.updateGrade(registrationId, grade);
        return "redirect:/instructor/course/" + courseId + "/edit?gradeSaved=true";
    }

    @PostMapping("/course/{id}/update")
    public String updateCourse(@PathVariable Long id,
                              @RequestParam String courseName,
                              @RequestParam String courseCode,
                              @RequestParam String description,
                              @RequestParam int maxSeats,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startDate,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endDate,
                              HttpSession session,
                              Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        Course existing = courseService.getCourseById(id);
        if (!existing.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/home?error=unauthorized";
        }

        try {
            Course updatedCourse = new Course();
            updatedCourse.setCourseName(courseName);
            updatedCourse.setCourseCode(courseCode.toUpperCase());
            updatedCourse.setDescription(description);
            updatedCourse.setMaxSeats(maxSeats);
            updatedCourse.setStartDate(startDate);
            updatedCourse.setEndDate(endDate);
            courseService.updateCourse(id, updatedCourse);
            return "redirect:/instructor/course/" + id + "/edit?courseUpdated=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("course", existing);
            model.addAttribute("sections", courseSectionService.getSectionsByCourseId(id));
            return "edit-course";
        }
    }

    @GetMapping("/add-course")
    public String addCoursePage(HttpSession session, Model model) {
        if (session.getAttribute("instructorId") == null) {
            return "redirect:/instructor/login";
        }
        return "add-course";
    }

    @PostMapping("/add-course")
    public String addCourse(
            @RequestParam String courseName,
            @RequestParam String courseCode,
            @RequestParam String description,
            @RequestParam int maxSeats,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endDate,
            HttpSession session,
            Model model) {

        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        try {
            Instructor instructor = instructorService.getInstructorById(instructorId);

            Course course = new Course();
            course.setCourseName(courseName);
            course.setCourseCode(courseCode.toUpperCase());
            course.setDescription(description);
            course.setMaxSeats(maxSeats);
            course.setNumEnrolled(0);
            course.setStartDate(startDate);
            course.setEndDate(endDate);
            course.setInstructor(instructor);

            courseService.createCourse(course);
            return "redirect:/instructor/home?added=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "add-course";
        }
    }

    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        Long instructorId = (Long) session.getAttribute("instructorId");
        if (instructorId == null) return "redirect:/instructor/login";

        try {
            // Verify the course belongs to this instructor before deleting
            Course course = courseService.getCourseById(id);
            if (!course.getInstructor().getId().equals(instructorId)) {
                model.addAttribute("error", "You can only delete your own courses");
                return "redirect:/instructor/home?error=unauthorized";
            }
            courseService.deleteCourse(id);
            return "redirect:/instructor/home?deleted=true";

        } catch (RuntimeException e) {
            return "redirect:/instructor/home?error=notfound";
        }
    }


    // ── Get email preview data for the modal ─────────
@GetMapping("/course/{id}/email-preview")
@ResponseBody
public ResponseEntity<Map<String, Object>> getEmailPreview(
        @PathVariable Long id,
        HttpSession session) {

    if (session.getAttribute("instructorId") == null) {
        return ResponseEntity.status(401).build();
    }

    try {
        Course course = courseService.getCourseById(id);
        List<Registration> registrations =
            registrationService.getCourseRegistrations(id);

        // Get only active students
        List<String> emails = registrations.stream()
            .filter(r -> r.getStatus().toString().equals("ACTIVE"))
            .map(r -> r.getStudent().getEmail())
            .collect(Collectors.toList());

        List<String> studentNames = registrations.stream()
            .filter(r -> r.getStatus().toString().equals("ACTIVE"))
            .map(r -> r.getStudent().getFirstName() + " "
                    + r.getStudent().getLastName())
            .collect(Collectors.toList());

        // Build default email content
        String defaultSubject = "Class Notification: " + course.getCourseName();
        String defaultBody = buildDefaultEmailBody(course,
            (String) session.getAttribute("instructorName"));

        Map<String, Object> response = new HashMap<>();
        response.put("emails", emails);
        response.put("studentNames", studentNames);
        response.put("subject", defaultSubject);
        response.put("body", defaultBody);
        response.put("courseName", course.getCourseName());
        response.put("recipientCount", emails.size());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        return ResponseEntity.status(500).build();
    }
}

// ── Send the email ────────────────────────────────
@PostMapping("/course/{id}/send-email")
@ResponseBody
public ResponseEntity<Map<String, String>> sendCourseEmail(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload,
        HttpSession session) {

    Map<String, String> response = new HashMap<>();

    if (session.getAttribute("instructorId") == null) {
        response.put("error", "Not authenticated");
        return ResponseEntity.status(401).body(response);
    }

    try {
        List<Registration> registrations =
            registrationService.getCourseRegistrations(id);

        List<String> emails = registrations.stream()
            .filter(r -> r.getStatus().toString().equals("ACTIVE"))
            .map(r -> r.getStudent().getEmail())
            .collect(Collectors.toList());

        if (emails.isEmpty()) {
            response.put("error", "No active students to email");
            return ResponseEntity.badRequest().body(response);
        }

        String subject = payload.get("subject");
        String body    = payload.get("body");

        if (subject == null || subject.isBlank()) {
            response.put("error", "Subject cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }

        if (body == null || body.isBlank()) {
            response.put("error", "Email body cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }

        emailService.sendBulkEmail(emails, subject, body);

        response.put("message",
            "Email sent successfully to " + emails.size() + " student(s)");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("error", "Failed to send email: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}

// ── Build the default email body ──────────────────
private String buildDefaultEmailBody(Course course, String instructorName) {
    return """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
            <div style="background: #1a237e; padding: 24px; border-radius: 8px 8px 0 0;">
                <h2 style="color: white; margin: 0;">Class Start Notification</h2>
            </div>
            <div style="background: #f9f9f9; padding: 24px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 8px 8px;">
                <p>Dear Student,</p>
                <p>This is a reminder that <strong>%s</strong> (%s) will begin on <strong>%s</strong>.</p>
                <table style="width:100%%; border-collapse: collapse; margin: 16px 0;">
                    <tr>
                        <td style="padding: 8px; background: #e8eaf6; font-weight: bold; width: 40%%;">Course</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; background: #e8eaf6; font-weight: bold;">Course Code</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; background: #e8eaf6; font-weight: bold;">Start Date</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; background: #e8eaf6; font-weight: bold;">End Date</td>
                        <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; background: #e8eaf6; font-weight: bold;">Instructor</td>
                        <td style="padding: 8px;">%s</td>
                    </tr>
                </table>
                <p>Please make sure you are prepared and ready for the first session.</p>
                <p>Best regards,<br><strong>%s</strong></p>
                <br>
                <p>This is an automated email, do not reply to this email.<br></p>
            </div>
        </div>
        """.formatted(
            course.getCourseName(), course.getCourseCode(),
            course.getStartDate(),
            course.getCourseName(),
            course.getCourseCode(),
            course.getStartDate(),
            course.getEndDate(),
            instructorName,
            instructorName
        );
}
}