package com.farmmart.controller;

import com.farmmart.model.User;
import com.farmmart.repository.UserRepository;
import com.farmmart.service.CloudinaryService;
import com.farmmart.service.EmailService;
import com.farmmart.util.JwtUtil;
import com.farmmart.util.OtpUtil;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * replaces: route/user.route.js  +  controllers/user.controller.js
 *
 * Every @GetMapping/@PostMapping/@PutMapping here directly mirrors
 * the routes defined in your Node.js user.route.js.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository       userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil              jwtUtil;
    private final EmailService         emailService;
    private final CloudinaryService    cloudinaryService;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/user/register
    // replaces: registerUserController
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name     = body.get("name");
        String email    = body.get("email");
        String password = body.get("password");

        if (name == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide name, email, password", "error", true, "success", false));
        }

        if (userRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Already register email", "error", true, "success", false));
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password)); // replaces: bcryptjs.hash(password, salt)
        User saved = userRepo.save(user);

        try {
            emailService.sendVerificationEmail(email, name, saved.getId());
        } catch (Exception e) {
            System.err.println("Could not send verification email: " + e.getMessage());
        }

        return ResponseEntity.ok(
            Map.of("message", "User register successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/user/verify-email   (called by clicking link in email)
    // replaces: verifyEmailController
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        User user = userRepo.findById(code)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code"));
        user.setVerifyEmail(true);
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Verify email done", "success", true, "error", false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/user/login
    // replaces: loginController
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body,
                                   HttpServletResponse response) {
        String email    = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide email, password", "error", true, "success", false));
        }

        User user = userRepo.findByEmail(email)
            .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "User not registered", "error", true, "success", false));
        }

        if (!"Active".equals(user.getStatus())) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Contact to Admin", "error", true, "success", false));
        }

        // replaces: bcryptjs.compare(password, user.password)
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Check your password", "error", true, "success", false));
        }

        String token = jwtUtil.generateToken(user.getId());
        user.setLastLoginDate(Instant.now());
        userRepo.save(user);

        // replaces: response.cookie('accessToken', accesstoken, { httpOnly, secure, sameSite })
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(18000) // 5 hours in seconds
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of(
            "message", "Login successfully",
            "error",   false,
            "success", true,
            "data",    Map.of("accesstoken", token)
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/user/logout
    // replaces: logoutController
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // replaces: response.clearCookie("accessToken", cookiesOption)
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logout successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/user/user-details
    // replaces: userDetails
    // Requires login — protected by SecurityConfig
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/user-details")
    public ResponseEntity<?> userDetails(Authentication auth) {
        // auth.getName() = userId — set by JwtAuthFilter, replaces: request.userId
        String userId = auth.getName();
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // replaces: .select('-password -refresh_token')
        user.setPassword(null);
        user.setRefreshToken(null);

        return ResponseEntity.ok(Map.of("message", "user details", "data", user, "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/user/upload-avatar
    // replaces: uploadAvatar
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                          Authentication auth) {
        String userId = auth.getName();
        String url = cloudinaryService.uploadImage(file);

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setAvatar(url);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
            "message", "upload profile",
            "success", true,
            "error",   false,
            "data",    Map.of("_id", userId, "avatar", url)
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/user/update-user
    // replaces: updateUserDetails
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> body,
                                        Authentication auth) {
        String userId = auth.getName();
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (body.get("name")     != null) user.setName(body.get("name"));
        if (body.get("email")    != null) user.setEmail(body.get("email"));
        if (body.get("mobile")   != null) user.setMobile(Long.parseLong(body.get("mobile")));
        if (body.get("password") != null) user.setPassword(encoder.encode(body.get("password")));

        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Updated successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/user/forgot-password
    // replaces: forgotPasswordController
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Email not available", "error", true, "success", false));
        }

        String otp = OtpUtil.generate();

        // FIXED BUG FROM NODE.JS:
        // Node had: new Date() + 60 * 60 * 1000  ← string concatenation, OTP never expired!
        // Spring fix: Instant.now().plus(1, HOURS) ← correct time arithmetic
        user.setForgotPasswordOtp(otp);
        user.setForgotPasswordExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
        userRepo.save(user);

        try {
            emailService.sendForgotPasswordEmail(email, user.getName(), otp);
        } catch (Exception e) {
            System.err.println("Could not send OTP email: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "check your email", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/user/verify-forgot-password-otp
    // replaces: verifyForgotPasswordOtp
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/verify-forgot-password-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp   = body.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide required field email, otp.", "error", true, "success", false));
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Email not available", "error", true, "success", false));
        }

        if (user.getForgotPasswordExpiry() == null ||
            user.getForgotPasswordExpiry().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Otp is expired", "error", true, "success", false));
        }

        if (!otp.equals(user.getForgotPasswordOtp())) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Invalid otp", "error", true, "success", false));
        }

        user.setForgotPasswordOtp(null);
        user.setForgotPasswordExpiry(null);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Verify otp successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/user/reset-password
    // replaces: resetpassword
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email       = body.get("email");
        String newPassword = body.get("newPassword");
        String confirmPwd  = body.get("confirmPassword");

        if (email == null || newPassword == null || confirmPwd == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "provide required fields email, newPassword, confirmPassword"));
        }

        if (!newPassword.equals(confirmPwd)) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "newPassword and confirmPassword must be same.", "error", true, "success", false));
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Email is not available", "error", true, "success", false));
        }

        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully.", "error", false, "success", true));
    }
}
