package com.sportpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * PerfilEsportivoRequest — DTO de entrada para RF007 (envio do perfil esportivo do atleta).
 * Esses dados são enviados ao webhook n8n para gerar o cronograma personalizado.
 */
@Data
public class PerfilEsportivoRequest {

    @NotNull(message = "ID do atleta é obrigatório")
    private Long atletaId;

    @NotNull(message = "ID do treinador é obrigatório")
    private Long treinadorId;

    @NotNull(message = "ID da modalidade é obrigatório")
    private Long modalidadeId;

    @NotBlank(message = "Objetivo é obrigatório")
    private String objetivo;

    /** INICIANTE, INTERMEDIARIO, AVANCADO */
    @NotBlank(message = "Experiência é obrigatória")
    private String experiencia;

    @NotNull(message = "Dias disponíveis é obrigatório")
    @Min(value = 1) @Max(value = 7)
    private Integer diasDisponiveis;

    private String limitacoesFisicas;
    private String observacoes;
}
