package com.sportpro.controller;

import com.sportpro.dto.request.TreinadorRequest;
import com.sportpro.dto.response.ApiResponseDto;
import com.sportpro.dto.response.TreinadorResponseDto;
import com.sportpro.service.TreinadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TreinadorController — Endpoints REST para gerenciamento de treinadores.
 *
 * Rotas:
 *   POST   /api/treinadores        → RF001: Cadastrar treinador
 *   GET    /api/treinadores        → Listar todos os treinadores
 *   GET    /api/treinadores/{id}   → Buscar treinador por ID
 *
 * @RestController = @Controller + @ResponseBody (retorna JSON automaticamente)
 * @RequestMapping define o prefixo base de todas as rotas
 */
@RestController
@RequestMapping("/api/treinadores")
@RequiredArgsConstructor
public class TreinadorController {

    private final TreinadorService treinadorService;

    /**
     * POST /api/treinadores
     * Cadastra um novo treinador. Retorna HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<TreinadorResponseDto>> cadastrar(
            @Valid @RequestBody TreinadorRequest request) {

        TreinadorResponseDto dto = treinadorService.cadastrar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Treinador cadastrado com sucesso!", dto));
    }

    /**
     * GET /api/treinadores
     * Lista todos os treinadores disponíveis.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TreinadorResponseDto>>> listar() {
        return ResponseEntity.ok(ApiResponseDto.ok("Treinadores listados.", treinadorService.listarTodos()));
    }

    /**
     * GET /api/treinadores/{id}
     * Retorna um treinador específico.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<TreinadorResponseDto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Treinador encontrado.", treinadorService.buscarPorId(id)));
    }
}
