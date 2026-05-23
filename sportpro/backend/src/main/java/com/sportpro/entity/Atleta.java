package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Atleta — Entidade JPA que representa um atleta cadastrado.
 *
 * Mapeada para a tabela `atletas`.
 * Um atleta se vincula a um treinador e possui cronogramas gerados.
 */
@Entity
@Table(name = "atletas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Hash BCrypt da senha */
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Integer idade;

    /** Peso em kg */
    @Column(nullable = false)
    private Double peso;

    /** Altura em metros (ex: 1.75) */
    @Column(nullable = false)
    private Double altura;

    /** Objetivo esportivo descrito pelo atleta */
    @Column(columnDefinition = "TEXT")
    private String objetivo;

    /** Nível de experiência: INICIANTE, INTERMEDIARIO, AVANCADO */
    @Column(length = 30)
    private String experiencia;

    /** Dias disponíveis por semana (1-7) */
    private Integer diasDisponiveis;

    /** Limitações físicas ou lesões */
    @Column(columnDefinition = "TEXT")
    private String limitacoesFisicas;

    /** Observações adicionais do atleta */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Modalidade escolhida pelo atleta.
     * ManyToOne: vários atletas podem escolher a mesma modalidade.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;

    /**
     * Treinador selecionado pelo atleta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treinador_id")
    private Treinador treinador;

    /**
     * Cronogramas gerados para este atleta.
     */
    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cronograma> cronogramas;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
