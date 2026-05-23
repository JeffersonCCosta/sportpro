package com.sportpro.controller;

import com.sportpro.dto.request.LoginRequest;
import com.sportpro.dto.response.ApiResponseDto;
import com.sportpro.dto.response.LoginResponseDto;
import com.sportpro.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — Endpoint de autenticação.
 *
 * POST /api/auth/login → autentica treinador ou atleta
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Body: { "email": "...", "senha": "...", "tipo": "TREINADOR|ATLETA" }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponseDto dto = authService.login(request);
        return ResponseEntity.ok(ApiResponseDto.ok("Login realizado com sucesso!", dto));
    }
}
