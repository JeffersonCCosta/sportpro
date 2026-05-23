package com.sportpro.service;

import com.sportpro.dto.request.MetodologiaRequest;
import com.sportpro.dto.response.MetodologiaResponseDto;
import com.sportpro.entity.Metodologia;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.MetodologiaRepository;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MetodologiaService — Lógica de negócio para metodologias de treino (RF003).
 */
@Service
@RequiredArgsConstructor
public class MetodologiaService {

    private final MetodologiaRepository metodologiaRepository;
    private final TreinadorRepository treinadorRepository;

    @Transactional
    public MetodologiaResponseDto cadastrar(MetodologiaRequest request) {
        Treinador treinador = treinadorRepository.findById(request.getTreinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado"));

        Metodologia metodologia = Metodologia.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .estrategias(request.getEstrategias())
                .recomendacoesAlimentares(request.getRecomendacoesAlimentares())
                .criteriosEvolucao(request.getCriteriosEvolucao())
                .treinador(treinador)
                .build();

        return toDto(metodologiaRepository.save(metodologia));
    }

    @Transactional(readOnly = true)
    public List<MetodologiaResponseDto> listarPorTreinador(Long treinadorId) {
        return metodologiaRepository.findByTreinadorId(treinadorId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private MetodologiaResponseDto toDto(Metodologia m) {
        return MetodologiaResponseDto.builder()
                .id(m.getId())
                .titulo(m.getTitulo())
                .descricao(m.getDescricao())
                .estrategias(m.getEstrategias())
                .recomendacoesAlimentares(m.getRecomendacoesAlimentares())
                .criteriosEvolucao(m.getCriteriosEvolucao())
                .treinadorId(m.getTreinador().getId())
                .criadoEm(m.getCriadoEm())
                .build();
    }
}
