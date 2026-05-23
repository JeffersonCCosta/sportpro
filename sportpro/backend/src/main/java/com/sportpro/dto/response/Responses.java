package com.sportpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// ===================================================================
// DTOs de resposta — o que o backend retorna ao frontend via JSON
// Não expõem dados sensíveis como senha ou hash
// ===================================================================

/**
 * TreinadorResponse — retornado nos endpoints GET /treinadores.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class TreinadorResponse {
    private Long id;
    private String nome;
    private String email;
    private String descricaoProfissional;
    private LocalDateTime criadoEm;
    private List<ModalidadeResponse> modalidades;
}

/**
 * AtletaResponse — retornado nos endpoints GET /atletas.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class AtletaResponse {
    private Long id;
    private String nome;
    private String email;
    private Integer idade;
    private Double peso;
    private Double altura;
    private String objetivo;
    private String experiencia;
    private Integer diasDisponiveis;
    private LocalDateTime criadoEm;
    private Long treinadorId;
    private String treinadorNome;
    private Long modalidadeId;
    private String modalidadeNome;
}

/**
 * ModalidadeResponse — retornado nos endpoints GET /modalidades.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class ModalidadeResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Long treinadorId;
    private String treinadorNome;
}

/**
 * MetodologiaResponse — retornado nos endpoints GET /metodologias.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class MetodologiaResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private String estrategias;
    private String recomendacoesAlimentares;
    private String criteriosEvolucao;
    private LocalDateTime criadoEm;
}

/**
 * CronogramaResponse — retornado ao atleta com o cronograma gerado pelo n8n.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class CronogramaResponse {
    private Long id;
    private Long atletaId;
    private String atletaNome;
    private String treinoSemanal;
    private String dieta;
    private String observacoes;
    private String status;
    private LocalDateTime criadoEm;
}

/**
 * LoginResponse — retornado após autenticação bem-sucedida.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class LoginResponse {
    private Long id;
    private String nome;
    private String email;
    private String tipo; // TREINADOR ou ATLETA
    private String mensagem;
}

/**
 * ApiResponse — wrapper genérico para respostas padronizadas da API.
 * Garante estrutura consistente: { success, message, data }
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
