package com.automatch.portal.controller;

import com.automatch.portal.mapper.UserMapper;
import com.automatch.portal.model.UserModel;
import com.automatch.portal.records.LoginRequestRecord;
import com.automatch.portal.records.TokenResponse;
import com.automatch.portal.records.UserRecord;
import com.automatch.portal.service.JwtService;
import com.automatch.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserRecord> register(@RequestBody UserRecord userRecord) {
        // Apenas cria o usuário, SEM token
        UserRecord savedUser = userService.save(userRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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
                        24 * 60 * 60L, // 24 horas em segundos
                        user
                )
        );
    }
}