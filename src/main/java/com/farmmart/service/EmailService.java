package com.farmmart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * replaces:
 *   - config/sendEmail.js          (the core send function using Resend)
 *   - utils/verifyEmailTemplate.js (HTML email for verification)
 *   - utils/forgotPasswordTemplate.js (HTML email for OTP)
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${frontend.url}")
    private String frontendUrl;

    private final WebClient.Builder webClientBuilder;

    /**
     * replaces: sendEmail({ sendTo, subject, html })
     * Calls the Resend REST API to send emails.
     */
    private void send(String to, String subject, String html) {
        try {
            WebClient client = webClientBuilder
                .baseUrl("https://api.resend.com")
                .build();

            Map<String, Object> body = Map.of(
                "from",    "FarmMart <onboarding@resend.dev>",
                "to",      List.of(to),
                "subject", subject,
                "html",    html
            );

            client.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> System.out.println("Email sent to: " + to),
                    error    -> System.err.println("Email failed: " + error.getMessage())
                );

        } catch (Exception e) {
            System.err.println("Email send error: " + e.getMessage());
        }
    }

    /**
     * replaces: utils/verifyEmailTemplate.js
     * Sends the email verification link after registration.
     */
    public void sendVerificationEmail(String to, String name, String userId) {
        String verifyUrl = frontendUrl + "/verify-email?code=" + userId;

        String html = "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px'>"
            + "<div style='background:#f0fdf4;border-radius:12px;padding:30px;text-align:center'>"
            + "<h1 style='color:#15803d;margin-bottom:8px'>FarmMart</h1>"
            + "<h2 style='color:#166534'>Hi " + name + "! Verify your email</h2>"
            + "<p style='color:#374151;font-size:16px'>Click the button below to verify your email address and activate your account.</p>"
            + "<a href='" + verifyUrl + "' "
            + "style='display:inline-block;margin-top:20px;background:#16a34a;color:white;"
            + "padding:14px 32px;border-radius:8px;text-decoration:none;font-size:16px;font-weight:bold'>"
            + "Verify Email</a>"
            + "<p style='color:#9ca3af;font-size:13px;margin-top:24px'>If you did not create an account, you can ignore this email.</p>"
            + "</div></body></html>";

        send(to, "Verify your FarmMart email", html);
    }

    /**
     * replaces: utils/forgotPasswordTemplate.js
     * Sends the OTP for password reset.
     */
    public void sendForgotPasswordEmail(String to, String name, String otp) {
        String html = "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px'>"
            + "<div style='background:#f0fdf4;border-radius:12px;padding:30px;text-align:center'>"
            + "<h1 style='color:#15803d;margin-bottom:8px'>FarmMart</h1>"
            + "<h2 style='color:#166534'>Password Reset OTP</h2>"
            + "<p style='color:#374151;font-size:16px'>Hi " + name + "! Use the OTP below to reset your password:</p>"
            + "<div style='background:#dcfce7;border:2px dashed #16a34a;border-radius:12px;padding:20px;margin:20px 0'>"
            + "<span style='font-size:40px;font-weight:bold;letter-spacing:12px;color:#15803d'>" + otp + "</span>"
            + "</div>"
            + "<p style='color:#dc2626;font-weight:bold'>This OTP expires in 1 hour.</p>"
            + "<p style='color:#9ca3af;font-size:13px'>If you did not request a password reset, please ignore this email.</p>"
            + "</div></body></html>";

        send(to, "Your FarmMart password reset OTP", html);
    }
}
