package com.sportpro.service;

import com.sportpro.dto.request.LoginRequest;
import com.sportpro.dto.response.LoginResponseDto;
import com.sportpro.entity.Atleta;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.BusinessException;
import com.sportpro.repository.AtletaRepository;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService — Autenticação de treinadores e atletas.
 *
 * Fluxo:
 * 1. Busca usuário pelo email e tipo (TREINADOR ou ATLETA)
 * 2. Compara senha informada com hash BCrypt armazenado
 * 3. Retorna dados do usuário (sem senha)
 *
 * JWT pode ser adicionado aqui no futuro:
 * - Gerar token no login
 * - Retornar token no LoginResponseDto
 * - Validar token em JwtFilter antes de cada requisição
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TreinadorRepository treinadorRepository;
    private final AtletaRepository atletaRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequest request) {
        return switch (request.getTipo().toUpperCase()) {
            case "TREINADOR" -> loginTreinador(request);
            case "ATLETA" -> loginAtleta(request);
            default -> throw new BusinessException("Tipo de usuário inválido: " + request.getTipo());
        };
    }

    private LoginResponseDto loginTreinador(LoginRequest request) {
        Treinador treinador = treinadorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha incorretos"));

        if (!passwordEncoder.matches(request.getSenha(), treinador.getSenha())) {
            throw new BusinessException("Email ou senha incorretos");
        }

        return LoginResponseDto.builder()
                .id(treinador.getId())
                .nome(treinador.getNome())
                .email(treinador.getEmail())
                .tipo("TREINADOR")
                .mensagem("Login realizado com sucesso!")
                .build();
    }

    private LoginResponseDto loginAtleta(LoginRequest request) {
        Atleta atleta = atletaRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha incorretos"));

        if (!passwordEncoder.matches(request.getSenha(), atleta.getSenha())) {
            throw new BusinessException("Email ou senha incorretos");
        }

        return LoginResponseDto.builder()
                .id(atleta.getId())
                .nome(atleta.getNome())
                .email(atleta.getEmail())
                .tipo("ATLETA")
                .mensagem("Login realizado com sucesso!")
                .build();
    }
}
