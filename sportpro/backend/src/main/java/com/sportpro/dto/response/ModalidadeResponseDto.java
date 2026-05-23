package com.sportpro.dto.response;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ModalidadeResponseDto {
    private Long id;
    private String nome;
    private String descricao;
    private Long treinadorId;
    private String treinadorNome;
}
