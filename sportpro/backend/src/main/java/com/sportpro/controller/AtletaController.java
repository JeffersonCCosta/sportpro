package com.sportpro.controller;

import com.sportpro.dto.request.AtletaRequest;
import com.sportpro.dto.request.PerfilEsportivoRequest;
import com.sportpro.dto.response.*;
import com.sportpro.service.AtletaService;
import com.sportpro.service.CronogramaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AtletaController — Endpoints REST para gerenciamento de atletas.
 *
 * Rotas:
 *   POST /api/atletas              → RF004: Cadastrar atleta
 *   GET  /api/atletas              → Listar todos
 *   GET  /api/atletas/{id}         → Buscar por ID
 *   PUT  /api/atletas/perfil       → RF007: Enviar perfil esportivo
 *   POST /api/atletas/{id}/cronograma → RF008: Gerar cronograma
 *   GET  /api/atletas/{id}/cronogramas → Histórico de cronogramas
 *   GET  /api/atletas/treinador/{id}  → Atletas por treinador
 */
@RestController
@RequestMapping("/api/atletas")
@RequiredArgsConstructor
public class AtletaController {

    private final AtletaService atletaService;
    private final CronogramaService cronogramaService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<AtletaResponseDto>> cadastrar(
            @Valid @RequestBody AtletaRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Atleta cadastrado com sucesso!", atletaService.cadastrar(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AtletaResponseDto>>> listar() {
        return ResponseEntity.ok(ApiResponseDto.ok("Atletas listados.", atletaService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AtletaResponseDto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Atleta encontrado.", atletaService.buscarPorId(id)));
    }

    /** RF007 — Atualiza perfil esportivo e vincula treinador/modalidade */
    @PutMapping("/perfil")
    public ResponseEntity<ApiResponseDto<AtletaResponseDto>> atualizarPerfil(
            @Valid @RequestBody PerfilEsportivoRequest request) {
        return ResponseEntity.ok(ApiResponseDto.ok("Perfil atualizado!", atletaService.atualizarPerfil(request)));
    }

    /** RF008 — Dispara geração do cronograma via n8n */
    @PostMapping("/{id}/cronograma")
    public ResponseEntity<ApiResponseDto<CronogramaResponseDto>> gerarCronograma(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Cronograma gerado com sucesso!", cronogramaService.gerarCronograma(id)));
    }

    /** Histórico de cronogramas do atleta */
    @GetMapping("/{id}/cronogramas")
    public ResponseEntity<ApiResponseDto<List<CronogramaResponseDto>>> listarCronogramas(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Cronogramas listados.", cronogramaService.listarPorAtleta(id)));
    }

    /** Lista atletas de um treinador */
    @GetMapping("/treinador/{treinadorId}")
    public ResponseEntity<ApiResponseDto<List<AtletaResponseDto>>> listarPorTreinador(@PathVariable Long treinadorId) {
        return ResponseEntity.ok(ApiResponseDto.ok("Atletas listados.", atletaService.listarPorTreinador(treinadorId)));
    }
}
