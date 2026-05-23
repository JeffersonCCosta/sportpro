package com.sportpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * MetodologiaRequest — DTO para cadastro de metodologia de treino (RF003).
 */
@Data
public class MetodologiaRequest {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String estrategias;
    private String recomendacoesAlimentares;
    private String criteriosEvolucao;

    @NotNull(message = "ID do treinador é obrigatório")
    private Long treinadorId;
}
