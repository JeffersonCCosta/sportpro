package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * PlanoTreino — Entidade que unifica Modalidade + Metodologia + Nutrição
 * em um único cadastro vinculado ao treinador.
 *
 * Nome da seção no sistema: "Planos de Treino"
 *
 * O treinador seleciona a modalidade (dropdown fixo) e o nível (dropdown fixo)
 * e preenche todas as informações de treino e nutrição de uma vez.
 *
 * Esta tabela é consumida diretamente pelo n8n para gerar cronogramas
 * personalizados com dados reais do treinador, sem alucinação da IA.
 */
@Entity
@Table(
    name = "planos_treino",
    uniqueConstraints = {
        // Um treinador só pode ter um plano por modalidade + nível
        @UniqueConstraint(
            name = "uk_plano_treinador_modalidade_nivel",
            columnNames = {"treinador_id", "modalidade", "nivel"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Treinador dono deste plano */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treinador_id", nullable = false)
    private Treinador treinador;

    // ----------------------------------------------------------------
    // IDENTIFICAÇÃO — selecionados via dropdown
    // ----------------------------------------------------------------

    /**
     * Modalidade esportiva — valores fixos definidos no frontend:
     * 100m rasos, 200m rasos, 400m rasos, 800m, 1500m,
     * 5000m, 10km, Meia-maratona, Maratona
     */
    @Column(nullable = false, length = 50)
    private String modalidade;

    /**
     * Nível do atleta — valores fixos:
     * INICIANTE | INTERMEDIARIO | AVANCADO
     */
    @Column(nullable = false, length = 20)
    private String nivel;

    // ----------------------------------------------------------------
    // TREINO
    // ----------------------------------------------------------------

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricaoGeral;

    @Column(columnDefinition = "TEXT")
    private String estruturaSemana;

    @Column(columnDefinition = "TEXT")
    private String tiposTreino;

    @Column(columnDefinition = "TEXT")
    private String intensidades;

    @Column(columnDefinition = "TEXT")
    private String exerciciosForca;

    @Column(columnDefinition = "TEXT")
    private String recuperacao;

    @Column(columnDefinition = "TEXT")
    private String metricasAvaliacao;

    @Column(columnDefinition = "TEXT")
    private String cuidadosEspeciais;

    private Integer diasSemanaMin;
    private Integer diasSemanaMax;

    @Column
    private Double volumeSemanalKm;

    // ----------------------------------------------------------------
    // NUTRIÇÃO
    // ----------------------------------------------------------------

    @Column(columnDefinition = "TEXT")
    private String distribuicaoMacros;

    @Column(columnDefinition = "TEXT")
    private String cafeDaManha;

    @Column(columnDefinition = "TEXT")
    private String almoco;

    @Column(columnDefinition = "TEXT")
    private String jantar;

    @Column(columnDefinition = "TEXT")
    private String preTreino;

    @Column(columnDefinition = "TEXT")
    private String posTreino;

    @Column(columnDefinition = "TEXT")
    private String duranteTreinoLongo;

    @Column(columnDefinition = "TEXT")
    private String hidratacao;

    @Column(columnDefinition = "TEXT")
    private String suplementacaoBase;

    // ----------------------------------------------------------------
    // CONTROLE
    // ----------------------------------------------------------------

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
