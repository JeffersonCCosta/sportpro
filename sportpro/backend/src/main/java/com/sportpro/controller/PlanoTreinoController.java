package com.sportpro.controller;

import com.sportpro.dto.request.PlanoTreinoRequest;
import com.sportpro.dto.response.ApiResponseDto;
import com.sportpro.dto.response.PlanoTreinoResponseDto;
import com.sportpro.service.PlanoTreinoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PlanoTreinoController — Endpoints REST para Planos de Treino.
 *
 * POST   /api/planos-treino                     → Criar ou atualizar plano
 * GET    /api/planos-treino/treinador/{id}       → Listar planos do treinador
 * GET    /api/planos-treino/{id}                 → Buscar plano por ID
 * DELETE /api/planos-treino/{id}                 → Excluir plano
 */
@RestController
@RequestMapping("/api/planos-treino")
@RequiredArgsConstructor
public class PlanoTreinoController {

    private final PlanoTreinoService planoTreinoService;

    /** Criar ou atualizar plano (se já existir modalidade+nível para o treinador, faz update) */
    @PostMapping
    public ResponseEntity<ApiResponseDto<PlanoTreinoResponseDto>> salvar(
            @Valid @RequestBody PlanoTreinoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Plano salvo com sucesso!", planoTreinoService.salvar(request)));
    }

    /** Lista todos os planos de um treinador */
    @GetMapping("/treinador/{treinadorId}")
    public ResponseEntity<ApiResponseDto<List<PlanoTreinoResponseDto>>> listarPorTreinador(
            @PathVariable Long treinadorId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Planos listados.", planoTreinoService.listarPorTreinador(treinadorId)));
    }

    /** Busca plano por ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PlanoTreinoResponseDto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Plano encontrado.", planoTreinoService.buscarPorId(id)));
    }

    /** Lista todos os planos (usado pelo frontend para montar seleção de treinador) */
    @GetMapping("/todos")
    public ResponseEntity<ApiResponseDto<List<PlanoTreinoResponseDto>>> listarTodos() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Planos listados.", planoTreinoService.listarTodos()));
    }

    /** Exclui plano */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> excluir(@PathVariable Long id) {
        planoTreinoService.excluir(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Plano excluído.", null));
    }
}
