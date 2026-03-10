package com.example.stockify.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);   // no display name = no UnsupportedEncodingException
        helper.setTo(toEmail);
        helper.setSubject("Stockify - Your Email Verification OTP");

        String html =
                "<div style='font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:28px;background:#0f172a;border-radius:14px;'>" +
                        "  <div style='text-align:center;margin-bottom:20px;'>" +
                        "    <div style='display:inline-block;background:#2563eb;border-radius:10px;padding:10px 18px;'>" +
                        "      <span style='color:white;font-size:22px;font-weight:700;'>Stockify</span>" +
                        "    </div>" +
                        "  </div>" +
                        "  <h2 style='color:white;font-size:20px;text-align:center;margin-bottom:8px;'>Verify Your Email Address</h2>" +
                        "  <p style='color:#94a3b8;font-size:14px;text-align:center;margin-bottom:28px;'>Enter the OTP below to verify your email</p>" +
                        "  <div style='background:#1e293b;border:2px dashed #2563eb;border-radius:12px;padding:28px;text-align:center;margin-bottom:24px;'>" +
                        "    <p style='color:#64748b;font-size:12px;letter-spacing:3px;text-transform:uppercase;margin-bottom:10px;'>Your One-Time Password</p>" +
                        "    <div style='color:#60a5fa;font-size:44px;font-weight:700;font-family:monospace;letter-spacing:12px;'>" + otp + "</div>" +
                        "  </div>" +
                        "  <div style='text-align:center;margin-bottom:20px;'>" +
                        "    <p style='color:#64748b;font-size:13px;margin:6px 0;'>Valid for <strong style='color:#e2e8f0;'>10 minutes</strong></p>" +
                        "    <p style='color:#64748b;font-size:13px;margin:6px 0;'>Do not share this OTP with anyone</p>" +
                        "  </div>" +
                        "  <hr style='border:none;border-top:1px solid #1e3a5f;margin:20px 0;'/>" +
                        "  <p style='color:#475569;font-size:12px;text-align:center;'>If you didn't request this, please ignore this email.</p>" +
                        "  <p style='color:#334155;font-size:11px;text-align:center;margin-top:8px;'>2026 Stockify. Virtual Trading Platform.</p>" +
                        "</div>";

        helper.setText(html, true);
        mailSender.send(message);
    }
}
