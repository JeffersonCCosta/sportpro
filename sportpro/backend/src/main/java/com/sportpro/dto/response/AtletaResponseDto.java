package com.sportpro.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtletaResponseDto {
    private Long id;
    private String nome;
    private String email;
    private Integer idade;
    private Double peso;
    private Double altura;
    private String objetivo;
    private String experiencia;
    private Integer diasDisponiveis;
    private LocalDateTime criadoEm;
    private Long treinadorId;
    private String treinadorNome;
    private String modalidadeNome;
}
