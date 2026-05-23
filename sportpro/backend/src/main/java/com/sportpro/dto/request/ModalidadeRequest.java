package com.sportpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ModalidadeRequest — DTO para cadastro de modalidade esportiva (RF002).
 */
@Data
public class ModalidadeRequest {

    @NotBlank(message = "Nome da modalidade é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "ID do treinador é obrigatório")
    private Long treinadorId;
}
