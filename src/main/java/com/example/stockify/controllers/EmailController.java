package com.example.stockify.controllers;

import com.example.stockify.services.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public ResponseEntity<String> testEmail(
            @RequestParam String to
    ) {

        String messageId = emailService.sendEmail(
                to,
                "Stockify Test Email",
                "Hello! This is a test email from Stockify using Amazon SES."
        );

        return ResponseEntity.ok(
                "Email sent successfully. Message ID: " + messageId
        );
    }
}