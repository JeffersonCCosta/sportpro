package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Treinador — Entidade JPA que representa um treinador cadastrado.
 * Possui vários PlanosTreino (modalidade + metodologia + nutrição unificados).
 */
@Entity
@Table(name = "treinadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(columnDefinition = "TEXT")
    private String descricaoProfissional;

    /**
     * Planos de treino do treinador (unifica modalidade + metodologia + nutrição).
     */
    @OneToMany(mappedBy = "treinador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlanoTreino> planosTreino;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
