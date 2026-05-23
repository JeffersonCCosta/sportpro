package com.sportpro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest — DTO de autenticação.
 * Recebe email + senha para validar o usuário (treinador ou atleta).
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    /** TREINADOR ou ATLETA */
    @NotBlank(message = "Tipo de usuário é obrigatório")
    private String tipo;
}
