package com.sportpro.service;

import com.sportpro.dto.request.TreinadorRequest;
import com.sportpro.dto.response.TreinadorResponseDto;
import com.sportpro.dto.response.ModalidadeResponseDto;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.BusinessException;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TreinadorService — Camada de negócio para operações de Treinador.
 *
 * Responsabilidades:
 * - Validar regras de negócio (email único, senha mínima etc.)
 * - Criptografar senha antes de persistir
 * - Converter entidades em DTOs de resposta
 * - Orquestrar chamadas ao repositório
 *
 * Princípio: controllers não conhecem entidades JPA, só DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TreinadorService {

    private final TreinadorRepository treinadorRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cadastra um novo treinador.
     * @throws BusinessException se email já estiver em uso
     */
    @Transactional
    public TreinadorResponseDto cadastrar(TreinadorRequest request) {
        log.info("Cadastrando treinador: {}", request.getEmail());

        // Verifica duplicidade de email
        if (treinadorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        // Monta a entidade — senha é hasheada com BCrypt
        Treinador treinador = Treinador.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .descricaoProfissional(request.getDescricaoProfissional())
                .build();

        Treinador salvo = treinadorRepository.save(treinador);
        log.info("Treinador cadastrado com ID: {}", salvo.getId());

        return toDto(salvo);
    }

    /**
     * Retorna todos os treinadores cadastrados.
     */
    @Transactional(readOnly = true)
    public List<TreinadorResponseDto> listarTodos() {
        return treinadorRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca treinador por ID.
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public TreinadorResponseDto buscarPorId(Long id) {
        Treinador treinador = treinadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado: ID " + id));
        return toDto(treinador);
    }

    /**
     * Converte entidade Treinador → TreinadorResponseDto.
     * Nunca expõe a senha ou informações internas do JPA.
     */
    private TreinadorResponseDto toDto(Treinador t) {
        List<ModalidadeResponseDto> modalidades = (t.getModalidades() != null)
                ? t.getModalidades().stream().map(m -> ModalidadeResponseDto.builder()
                        .id(m.getId())
                        .nome(m.getNome())
                        .descricao(m.getDescricao())
                        .treinadorId(t.getId())
                        .treinadorNome(t.getNome())
                        .build())
                  .collect(Collectors.toList())
                : Collections.emptyList();

        return TreinadorResponseDto.builder()
                .id(t.getId())
                .nome(t.getNome())
                .email(t.getEmail())
                .descricaoProfissional(t.getDescricaoProfissional())
                .criadoEm(t.getCriadoEm())
                .modalidades(modalidades)
                .build();
    }
}
