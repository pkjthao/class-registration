package com.example.class_registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Send one email to multiple recipients
    public void sendBulkEmail(List<String> toEmails,
                              String subject,
                              String body) throws Exception {
        for (String email : toEmails) {
            sendSingleEmail(email, subject, body);
        }
    }

    // Send a single email
    public void sendSingleEmail(String toEmail,
                                String subject,
                                String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML content allowed
        mailSender.send(message);
    }
}