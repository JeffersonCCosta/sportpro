package com.sportpro.service;

import com.sportpro.dto.request.ModalidadeRequest;
import com.sportpro.dto.response.ModalidadeResponseDto;
import com.sportpro.entity.Modalidade;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.ModalidadeRepository;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ModalidadeService — Lógica de negócio para modalidades esportivas (RF002).
 */
@Service
@RequiredArgsConstructor
public class ModalidadeService {

    private final ModalidadeRepository modalidadeRepository;
    private final TreinadorRepository treinadorRepository;

    @Transactional
    public ModalidadeResponseDto cadastrar(ModalidadeRequest request) {
        Treinador treinador = treinadorRepository.findById(request.getTreinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado"));

        Modalidade modalidade = Modalidade.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .treinador(treinador)
                .build();

        return toDto(modalidadeRepository.save(modalidade));
    }

    @Transactional(readOnly = true)
    public List<ModalidadeResponseDto> listarTodas() {
        return modalidadeRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModalidadeResponseDto> listarPorTreinador(Long treinadorId) {
        return modalidadeRepository.findByTreinadorId(treinadorId).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    private ModalidadeResponseDto toDto(Modalidade m) {
        return ModalidadeResponseDto.builder()
                .id(m.getId())
                .nome(m.getNome())
                .descricao(m.getDescricao())
                .treinadorId(m.getTreinador().getId())
                .treinadorNome(m.getTreinador().getNome())
                .build();
    }
}
