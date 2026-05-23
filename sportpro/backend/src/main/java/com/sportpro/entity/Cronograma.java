package com.sportpro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Cronograma — Entidade que armazena o cronograma personalizado gerado pelo n8n.
 *
 * Fluxo:
 *  1. Atleta envia perfil → backend monta payload
 *  2. Backend chama webhook n8n via HTTP POST
 *  3. n8n processa (podendo usar IA) e retorna JSON com treino + dieta
 *  4. Backend salva este Cronograma no banco
 *  5. Frontend exibe o cronograma para o atleta
 */
@Entity
@Table(name = "cronogramas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Atleta dono deste cronograma */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    /**
     * Cronograma de treino semanal retornado pelo n8n.
     * Armazenado como TEXT para suportar JSON grande.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String treinoSemanal;

    /**
     * Plano alimentar/dieta retornado pelo n8n.
     */
    @Column(columnDefinition = "TEXT")
    private String dieta;

    /**
     * Observações gerais do n8n (ou do treinador via n8n).
     */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /** Status: PENDENTE, GERADO, ERRO */
    @Column(length = 20)
    private String status;

    /** Data de geração do cronograma */
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        this.criadoEm = LocalDateTime.now();
        if (this.status == null) this.status = "PENDENTE";
    }
}
