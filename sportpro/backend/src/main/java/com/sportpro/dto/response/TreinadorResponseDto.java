package com.sportpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * TreinadorResponseDto — DTO de saída para Treinador.
 * Atualizado: removida lista de modalidades (substituída por PlanosTreino).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreinadorResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String descricaoProfissional;
    private LocalDateTime criadoEm;
}
