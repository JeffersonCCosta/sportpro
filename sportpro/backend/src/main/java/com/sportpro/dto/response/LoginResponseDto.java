package com.sportpro.dto.response;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String tipo;
    private String mensagem;
}
