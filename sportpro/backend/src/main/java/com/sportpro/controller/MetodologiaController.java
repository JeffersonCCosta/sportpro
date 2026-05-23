package com.sportpro.controller;

import com.sportpro.dto.request.MetodologiaRequest;
import com.sportpro.dto.response.ApiResponseDto;
import com.sportpro.dto.response.MetodologiaResponseDto;
import com.sportpro.service.MetodologiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MetodologiaController — Endpoints para metodologias de treino (RF003).
 */
@RestController
@RequestMapping("/api/metodologias")
@RequiredArgsConstructor
public class MetodologiaController {

    private final MetodologiaService metodologiaService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<MetodologiaResponseDto>> cadastrar(
            @Valid @RequestBody MetodologiaRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Metodologia cadastrada!", metodologiaService.cadastrar(request)));
    }

    @GetMapping("/treinador/{treinadorId}")
    public ResponseEntity<ApiResponseDto<List<MetodologiaResponseDto>>> listarPorTreinador(
            @PathVariable Long treinadorId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Metodologias listadas.", metodologiaService.listarPorTreinador(treinadorId)));
    }
}
