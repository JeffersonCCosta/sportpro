package com.sportpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * AtletaRequest — DTO de entrada para cadastro de atleta (RF004).
 */
@Data
public class AtletaRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 10, message = "Idade mínima: 10 anos")
    @Max(value = 100, message = "Idade máxima: 100 anos")
    private Integer idade;

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "30.0", message = "Peso mínimo: 30 kg")
    private Double peso;

    @NotNull(message = "Altura é obrigatória")
    @DecimalMin(value = "1.0", message = "Altura mínima: 1.0 m")
    @DecimalMax(value = "2.5", message = "Altura máxima: 2.5 m")
    private Double altura;
}
