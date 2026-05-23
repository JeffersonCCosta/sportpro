package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Treinador — Entidade JPA que representa um treinador cadastrado.
 *
 * Mapeada para a tabela `treinadores` no MySQL.
 * Um treinador possui várias modalidades e metodologias.
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

    /** Email único — usado no login */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Senha armazenada como hash BCrypt — nunca texto puro */
    @Column(nullable = false)
    private String senha;

    @Column(columnDefinition = "TEXT")
    private String descricaoProfissional;

    /**
     * Relacionamento 1:N com modalidades.
     * cascade = ALL: operações no treinador propagam para modalidades.
     * orphanRemoval = true: remove modalidades sem treinador.
     */
    @OneToMany(mappedBy = "treinador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Modalidade> modalidades;

    /**
     * Relacionamento 1:N com metodologias de treino.
     */
    @OneToMany(mappedBy = "treinador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Metodologia> metodologias;

    /** Data de cadastro — preenchida automaticamente */
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
