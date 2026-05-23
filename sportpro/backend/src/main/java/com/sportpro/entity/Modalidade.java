package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Modalidade — Entidade que representa uma modalidade esportiva.
 *
 * Ex: 100m rasos, corrida de rua, natação.
 * Cada modalidade pertence a um treinador específico.
 */
@Entity
@Table(name = "modalidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    /**
     * Treinador responsável por esta modalidade.
     * FetchType.LAZY: carrega o treinador apenas quando acessado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treinador_id", nullable = false)
    private Treinador treinador;
}
