package com.sportpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * PlanoTreinoResponseDto — DTO de saída para Plano de Treino.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoTreinoResponseDto {

    private Long id;
    private Long treinadorId;
    private String treinadorNome;

    // Identificação
    private String modalidade;
    private String nivel;

    // Treino
    private String descricaoGeral;
    private String estruturaSemana;
    private String tiposTreino;
    private String intensidades;
    private String exerciciosForca;
    private String recuperacao;
    private String metricasAvaliacao;
    private String cuidadosEspeciais;
    private Integer diasSemanaMin;
    private Integer diasSemanaMax;
    private Double volumeSemanalKm;

    // Nutrição
    private String distribuicaoMacros;
    private String cafeDaManha;
    private String almoco;
    private String jantar;
    private String preTreino;
    private String posTreino;
    private String duranteTreinoLongo;
    private String hidratacao;
    private String suplementacaoBase;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
