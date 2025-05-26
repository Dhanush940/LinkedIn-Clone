package com.linkedin.backend.features.authentication.controller;

import com.linkedin.backend.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.backend.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.backend.features.authentication.model.AuthenticationUser;
import com.linkedin.backend.features.authentication.service.AuthenticationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import java.io.UnsupportedEncodingException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationUserService;

    public AuthenticationController(AuthenticationService authenticationUserService) {
        this.authenticationUserService = authenticationUserService;
    }
 
    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationUserService.login(loginRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody) {
        return authenticationUserService.register(registerRequestBody);
    }

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser){
        return authenticationUserService.getUserByEmail(authenticationUser.getEmail());
    }

    @PutMapping("/validate-email-verification-token")
    public String verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.validateEmailVerificationToken(token, user.getEmail());
        return new String("Email verified successfully.");
    }

    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.sendEmailVerificationToken(user.getEmail());
        return new String("Email verification token sent successfully.");
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authenticationUserService.sendPasswordResetToken(email);
        return new String("Password reset token sent successfully.");
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, @RequestParam String token,
            @RequestParam String email) {
        authenticationUserService.resetPassword(email, newPassword, token);
        return new String("Password reset successfully.");
    }
}
