package com.sportpro.dto.response;
import lombok.*;

/**
 * ApiResponseDto — Wrapper genérico para todas as respostas da API.
 *
 * Garante estrutura consistente:
 * { "success": true, "message": "...", "data": { ... } }
 *
 * Uso nos controllers:
 *   return ResponseEntity.ok(ApiResponseDto.ok("Cadastrado!", treinadorDto));
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApiResponseDto<T> {

    private boolean success;
    private String message;
    private T data;

    /** Fábrica para respostas de sucesso */
    public static <T> ApiResponseDto<T> ok(String message, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /** Fábrica para respostas de erro */
    public static <T> ApiResponseDto<T> error(String message) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
