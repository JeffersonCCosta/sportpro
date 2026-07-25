package com.sportpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * PlanoTreinoRequest — DTO de entrada para cadastro/atualização de Plano de Treino.
 * Unifica modalidade, metodologia e nutrição em um único formulário.
 */
@Data
public class PlanoTreinoRequest {

    @NotNull(message = "ID do treinador é obrigatório")
    private Long treinadorId;

    /**
     * Valores aceitos (dropdown fixo no frontend):
     * 100m rasos | 200m rasos | 400m rasos | 800m | 1500m
     * 5000m | 10km | Meia-maratona | Maratona
     */
    @NotBlank(message = "Modalidade é obrigatória")
    private String modalidade;

    /**
     * Valores aceitos (dropdown fixo):
     * INICIANTE | INTERMEDIARIO | AVANCADO
     */
    @NotBlank(message = "Nível é obrigatório")
    private String nivel;

    // --- Treino ---
    @NotBlank(message = "Descrição geral é obrigatória")
    private String descricaoGeral;

    private String estruturaSemana;
    private String tiposTreino;
    private String intensidades;
    private String exerciciosForca;
    private String recuperacao;
    private String metricasAvaliacao;
    private String cuidadosEspeciais;

    @Min(value = 1) @Max(value = 7)
    private Integer diasSemanaMin;

    @Min(value = 1) @Max(value = 7)
    private Integer diasSemanaMax;

    private Double volumeSemanalKm;

    // --- Nutrição ---
    private String distribuicaoMacros;
    private String cafeDaManha;
    private String almoco;
    private String jantar;
    private String preTreino;
    private String posTreino;
    private String duranteTreinoLongo;
    private String hidratacao;
    private String suplementacaoBase;
}
