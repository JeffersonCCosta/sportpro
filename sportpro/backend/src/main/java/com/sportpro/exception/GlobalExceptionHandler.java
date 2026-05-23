package com.sportpro.exception;

import com.sportpro.dto.response.ApiResponseDto;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler — Tratamento centralizado de exceções.
 *
 * @ControllerAdvice intercepta exceções lançadas em qualquer controller
 * e retorna respostas JSON padronizadas, evitando stack traces expostos.
 *
 * Princípio: a API nunca deve retornar erros não tratados ao cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação Bean Validation (@NotBlank, @Email etc.)
     * Retorna mapa de campos com seus erros: { "email": "Email inválido" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error("Erro de validação: verifique os campos"));
    }

    /**
     * Trata recurso não encontrado — 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    /**
     * Trata regras de negócio — 400
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(ex.getMessage()));
    }

    /**
     * Fallback para erros inesperados — 500
     * Em produção, logar o erro sem expor detalhes ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneral(Exception ex) {
        ex.printStackTrace(); // Em produção: usar logger
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error("Erro interno do servidor. Tente novamente."));
    }
}
