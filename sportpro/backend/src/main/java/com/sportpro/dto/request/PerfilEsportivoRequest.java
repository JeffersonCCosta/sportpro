package com.sportpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * PerfilEsportivoRequest — DTO de entrada para RF007.
 *
 * Atualizado: modalidade agora é recebida como NOME (string fixa)
 * ao invés de modalidadeId (Long), pois as modalidades são
 * definidas por dropdown fixo no frontend e não precisam de FK.
 */
@Data
public class PerfilEsportivoRequest {

    @NotNull(message = "ID do atleta é obrigatório")
    private Long atletaId;

    @NotNull(message = "ID do treinador é obrigatório")
    private Long treinadorId;

    /** Nome da modalidade — valor fixo do dropdown: "100m rasos", "Maratona" etc. */
    @NotBlank(message = "Modalidade é obrigatória")
    private String modalidadeNome;

    @NotBlank(message = "Objetivo é obrigatório")
    private String objetivo;

    /** INICIANTE | INTERMEDIARIO | AVANCADO */
    @NotBlank(message = "Experiência é obrigatória")
    private String experiencia;

    @NotNull(message = "Dias disponíveis é obrigatório")
    @Min(value = 1) @Max(value = 7)
    private Integer diasDisponiveis;

    private String limitacoesFisicas;
    private String observacoes;
}
