package com.sportpro.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MetodologiaResponseDto {
    private Long id;
    private String titulo;
    private String descricao;
    private String estrategias;
    private String recomendacoesAlimentares;
    private String criteriosEvolucao;
    private Long treinadorId;
    private LocalDateTime criadoEm;
}
