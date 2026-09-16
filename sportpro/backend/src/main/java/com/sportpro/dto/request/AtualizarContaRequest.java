package com.sportpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AtualizarContaRequest — DTO para edição de nome e/ou senha da conta.
 * Usado tanto por Atleta quanto por Treinador.
 *
 * senhaAtual e novaSenha são opcionais: só são obrigatórios quando o
 * usuário deseja trocar a senha. Se novaSenha vier vazia, apenas o
 * nome é atualizado.
 */
@Data
public class AtualizarContaRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    private String senhaAtual;

    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String novaSenha;
}
