package com.linkedin.backend.features.authentication.controller;

import com.linkedin.backend.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.backend.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.backend.features.authentication.model.AuthenticationUser;
import com.linkedin.backend.features.authentication.service.AuthenticationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @PutMapping("/profile/{id}")
    public AuthenticationUser updateUserProfile(
            @RequestAttribute("authenticatedUser") AuthenticationUser user,
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String about) {

        if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User does not have permission to update this profile.");
        }

        return authenticationUserService.updateUserProfile(
                user,
                firstName, lastName, company, position, location, about);
    }

    // @PutMapping("/profile/{id}/profile-picture")
    // public AuthenticationUser updateProfilePicture(
    //         @RequestAttribute("authenticatedUser") AuthenticationUser user,
    //         @PathVariable Long id,
    //         @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {

    //     if (!user.getId().equals(id)) {
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN,
    //                 "User does not have permission to update this profile picture.");
    //     }

    //     return authenticationUserService.updateProfilePicture(user, profilePicture);
    // }

    // @PutMapping("/profile/{id}/cover-picture")
    // public AuthenticationUser updateCoverPicture(
    //         @RequestAttribute("authenticatedUser") AuthenticationUser user,
    //         @PathVariable Long id,
    //         @RequestParam(required = false) MultipartFile coverPicture) throws IOException {

    //     if (!user.getId().equals(id)) {
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN,
    //                 "User does not have permission to update this cover picture.");
    //     }

    //     return authenticationUserService.updateCoverPicture(user, coverPicture);
    // }

    // @GetMapping("/users/me")
    // public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
    //     return user;
    // }

    @GetMapping("/users/{id}")
    public AuthenticationUser getUserById(@PathVariable Long id) {
        return authenticationUserService.getUserById(id);
    }

     @DeleteMapping("/delete")
    public String deleteUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.deleteUser(user.getId());
        return "User deleted successfully.";
    }



}
