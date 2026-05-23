package com.sportpro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * TreinadorRequest — DTO de entrada para cadastro/atualização de treinador.
 *
 * DTOs (Data Transfer Objects) isolam a API da entidade JPA,
 * permitindo validações e controle exato dos dados recebidos.
 */
@Data
public class TreinadorRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String descricaoProfissional;
}
