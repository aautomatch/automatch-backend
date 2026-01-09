package com.automatch.portal.controller;

import com.automatch.portal.enums.UserRole;
import com.automatch.portal.mapper.UserMapper;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.records.*;
import com.automatch.portal.service.InstructorService;
import com.automatch.portal.service.JwtService;
import com.automatch.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final InstructorService instructorService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody UserRecord userRecord) {
        try {
            UserRecord savedUser = userService.save(userRecord);

            if (savedUser.role() == UserRole.INSTRUCTOR) {
                createInstructorProfile(savedUser);
            }

            UserModel userModel = UserMapper.fromRecord(savedUser);
            String token = jwtService.generateToken(userModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new TokenResponse(
                            token,
                            "Bearer",
                            24 * 60 * 60L,
                            savedUser
                    )
            );
        } catch (Exception e) {
            System.err.println("Erro no registro: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestRecord loginRequest) {
        UserRecord user = userService.login(loginRequest);
        UserModel userModel = UserMapper.fromRecord(user);
        String token = jwtService.generateToken(userModel);
        return ResponseEntity.ok(
                new TokenResponse(
                        token,
                        "Bearer",
                        24 * 60 * 60L,
                        user
                )
        );
    }

    private void createInstructorProfile(UserRecord user) {
        try {
            InstructorRecord instructorRecord = new InstructorRecord(
                    user,
                    new BigDecimal("50.00"),
                    "Instrutor de direção",
                    0,
                    false,
                    new BigDecimal("0.00"),
                    0,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );

            instructorService.save(instructorRecord);
        } catch (Exception e) {
            System.err.println("Erro ao criar perfil de instrutor: " + e.getMessage());
        }
    }
}