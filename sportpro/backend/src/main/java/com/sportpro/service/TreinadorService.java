package com.sportpro.service;

import com.sportpro.dto.request.TreinadorRequest;
import com.sportpro.dto.response.TreinadorResponseDto;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.BusinessException;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TreinadorService — Camada de negócio para operações de Treinador.
 *
 * Atualizado: removida dependência de Modalidade e Metodologia.
 * O treinador agora gerencia PlanosTreino (unificado).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TreinadorService {

    private final TreinadorRepository treinadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TreinadorResponseDto cadastrar(TreinadorRequest request) {
        log.info("Cadastrando treinador: {}", request.getEmail());

        if (treinadorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

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

    @Transactional(readOnly = true)
    public List<TreinadorResponseDto> listarTodos() {
        return treinadorRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TreinadorResponseDto buscarPorId(Long id) {
        Treinador treinador = treinadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado: ID " + id));
        return toDto(treinador);
    }

    private TreinadorResponseDto toDto(Treinador t) {
        return TreinadorResponseDto.builder()
                .id(t.getId())
                .nome(t.getNome())
                .email(t.getEmail())
                .descricaoProfissional(t.getDescricaoProfissional())
                .criadoEm(t.getCriadoEm())
                .build();
    }
}
