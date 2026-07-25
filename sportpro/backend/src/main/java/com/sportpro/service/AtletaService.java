package com.sportpro.service;

import com.sportpro.dto.request.AtletaRequest;
import com.sportpro.dto.request.PerfilEsportivoRequest;
import com.sportpro.dto.response.AtletaResponseDto;
import com.sportpro.entity.*;
import com.sportpro.exception.BusinessException;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtletaService {

    private final AtletaRepository atletaRepository;
    private final TreinadorRepository treinadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AtletaResponseDto cadastrar(AtletaRequest request) {
        log.info("Cadastrando atleta: {}", request.getEmail());

        if (atletaRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        Atleta atleta = Atleta.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .idade(request.getIdade())
                .peso(request.getPeso())
                .altura(request.getAltura())
                .build();

        return toDto(atletaRepository.save(atleta));
    }

    /**
     * Atualiza perfil esportivo do atleta.
     * Modalidade agora é salva como String (nome) — sem FK para tabela de modalidades.
     */
    @Transactional
    public AtletaResponseDto atualizarPerfil(PerfilEsportivoRequest request) {
        Atleta atleta = atletaRepository.findById(request.getAtletaId())
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado"));

        Treinador treinador = treinadorRepository.findById(request.getTreinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado"));

        atleta.setObjetivo(request.getObjetivo());
        atleta.setExperiencia(request.getExperiencia());
        atleta.setDiasDisponiveis(request.getDiasDisponiveis());
        atleta.setLimitacoesFisicas(request.getLimitacoesFisicas());
        atleta.setObservacoes(request.getObservacoes());
        atleta.setTreinador(treinador);
        atleta.setModalidadeNome(request.getModalidadeNome());

        return toDto(atletaRepository.save(atleta));
    }

    @Transactional(readOnly = true)
    public List<AtletaResponseDto> listarTodos() {
        return atletaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtletaResponseDto buscarPorId(Long id) {
        return toDto(atletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado: ID " + id)));
    }

    @Transactional(readOnly = true)
    public List<AtletaResponseDto> listarPorTreinador(Long treinadorId) {
        return atletaRepository.findByTreinadorId(treinadorId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private AtletaResponseDto toDto(Atleta a) {
        return AtletaResponseDto.builder()
                .id(a.getId())
                .nome(a.getNome())
                .email(a.getEmail())
                .idade(a.getIdade())
                .peso(a.getPeso())
                .altura(a.getAltura())
                .objetivo(a.getObjetivo())
                .experiencia(a.getExperiencia())
                .diasDisponiveis(a.getDiasDisponiveis())
                .criadoEm(a.getCriadoEm())
                .treinadorId(a.getTreinador() != null ? a.getTreinador().getId() : null)
                .treinadorNome(a.getTreinador() != null ? a.getTreinador().getNome() : null)
                .modalidadeNome(a.getModalidadeNome())
                .build();
    }
}
