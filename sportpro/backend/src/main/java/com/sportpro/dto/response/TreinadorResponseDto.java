package com.sportpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ===================================================================
// Arquivo com todos os DTOs de resposta públicos (public classes)
// Cada DTO é uma classe separada para uso nos controllers e services
// ===================================================================

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TreinadorResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String descricaoProfissional;
    private LocalDateTime criadoEm;
    private List<ModalidadeResponseDto> modalidades;
}
