package com.sportpro.controller;

import com.sportpro.dto.request.ModalidadeRequest;
import com.sportpro.dto.response.ApiResponseDto;
import com.sportpro.dto.response.ModalidadeResponseDto;
import com.sportpro.service.ModalidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ModalidadeController — Endpoints para modalidades esportivas (RF002, RF005).
 *
 * GET  /api/modalidades                       → Listar todas
 * GET  /api/modalidades/treinador/{id}        → RF006: Modalidades por treinador
 * POST /api/modalidades                       → RF002: Cadastrar modalidade
 */
@RestController
@RequestMapping("/api/modalidades")
@RequiredArgsConstructor
public class ModalidadeController {

    private final ModalidadeService modalidadeService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<ModalidadeResponseDto>> cadastrar(
            @Valid @RequestBody ModalidadeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Modalidade cadastrada!", modalidadeService.cadastrar(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ModalidadeResponseDto>>> listar() {
        return ResponseEntity.ok(ApiResponseDto.ok("Modalidades listadas.", modalidadeService.listarTodas()));
    }

    @GetMapping("/treinador/{treinadorId}")
    public ResponseEntity<ApiResponseDto<List<ModalidadeResponseDto>>> listarPorTreinador(
            @PathVariable Long treinadorId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Modalidades do treinador.", modalidadeService.listarPorTreinador(treinadorId)));
    }
}
