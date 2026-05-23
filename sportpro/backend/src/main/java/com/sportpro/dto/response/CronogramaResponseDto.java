package com.sportpro.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CronogramaResponseDto {
    private Long id;
    private Long atletaId;
    private String atletaNome;
    private String treinoSemanal;
    private String dieta;
    private String observacoes;
    private String status;
    private LocalDateTime criadoEm;
}
