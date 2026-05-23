package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Metodologia — Entidade que representa a metodologia de treino de um treinador.
 *
 * Contém estratégias, recomendações alimentares e critérios de evolução
 * definidos pelo treinador para uma modalidade específica.
 */
@Entity
@Table(name = "metodologias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metodologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    /** Descrição geral da metodologia adotada */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    /** Estratégias de treino específicas */
    @Column(columnDefinition = "TEXT")
    private String estrategias;

    /** Recomendações nutricionais/alimentares */
    @Column(columnDefinition = "TEXT")
    private String recomendacoesAlimentares;

    /** Critérios usados para medir a evolução do atleta */
    @Column(columnDefinition = "TEXT")
    private String criteriosEvolucao;

    /** Treinador autor desta metodologia */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treinador_id", nullable = false)
    private Treinador treinador;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
