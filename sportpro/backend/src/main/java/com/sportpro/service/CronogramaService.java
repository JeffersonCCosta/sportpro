package com.sportpro.service;

import com.sportpro.dto.response.CronogramaResponseDto;
import com.sportpro.entity.*;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.integration.N8nWebhookIntegration;
import com.sportpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CronogramaService — Orquestra a geração de cronogramas personalizados.
 *
 * Fluxo principal (RF008):
 * 1. Recebe atletaId
 * 2. Carrega dados completos do atleta, treinador e metodologia
 * 3. Monta payload JSON estruturado
 * 4. Envia ao n8n via N8nWebhookIntegration
 * 5. Persiste o cronograma retornado
 * 6. Retorna DTO ao controller
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CronogramaService {

    private final CronogramaRepository cronogramaRepository;
    private final AtletaRepository atletaRepository;
    private final MetodologiaRepository metodologiaRepository;
    private final N8nWebhookIntegration n8nIntegration;

    /**
     * Gera cronograma personalizado para o atleta.
     */
    @Transactional
    public CronogramaResponseDto gerarCronograma(Long atletaId) {
        log.info("Iniciando geração de cronograma para atleta ID: {}", atletaId);

        // 1. Carrega atleta com todas as associações necessárias
        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado: ID " + atletaId));

        if (atleta.getTreinador() == null) {
            throw new ResourceNotFoundException("Atleta não possui treinador vinculado. Complete o perfil esportivo.");
        }

        // 2. Busca metodologia do treinador (a mais recente)
        List<Metodologia> metodologias = metodologiaRepository
                .findByTreinadorId(atleta.getTreinador().getId());

        Metodologia metodologia = metodologias.isEmpty() ? null : metodologias.get(0);

        // 3. Monta o payload que será enviado ao n8n
        Map<String, Object> payload = montarPayload(atleta, metodologia);

        // 4. Chama o webhook do n8n
        Map<String, Object> resultado = n8nIntegration.enviarParaN8n(payload);

        // 5. Persiste o cronograma recebido
        Cronograma cronograma = Cronograma.builder()
                .atleta(atleta)
                .treinoSemanal(String.valueOf(resultado.getOrDefault("treinoSemanal", "")))
                .dieta(String.valueOf(resultado.getOrDefault("dieta", "")))
                .observacoes(String.valueOf(resultado.getOrDefault("observacoes", "")))
                .status("GERADO")
                .build();

        Cronograma salvo = cronogramaRepository.save(cronograma);
        log.info("Cronograma salvo com ID: {}", salvo.getId());

        return toDto(salvo);
    }

    /**
     * Retorna histórico de cronogramas de um atleta.
     */
    @Transactional(readOnly = true)
    public List<CronogramaResponseDto> listarPorAtleta(Long atletaId) {
        return cronogramaRepository.findByAtletaIdOrderByCriadoEmDesc(atletaId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Monta o payload JSON enviado ao n8n.
     * Inclui dados do atleta, treinador e metodologia para personalização.
     */
    private Map<String, Object> montarPayload(Atleta atleta, Metodologia metodologia) {
        Map<String, Object> dadosAtleta = new LinkedHashMap<>();
        dadosAtleta.put("id", atleta.getId());
        dadosAtleta.put("nome", atleta.getNome());
        dadosAtleta.put("idade", atleta.getIdade());
        dadosAtleta.put("peso", atleta.getPeso());
        dadosAtleta.put("altura", atleta.getAltura());
        dadosAtleta.put("objetivo", atleta.getObjetivo());
        dadosAtleta.put("experiencia", atleta.getExperiencia());
        dadosAtleta.put("diasDisponiveis", atleta.getDiasDisponiveis());
        dadosAtleta.put("limitacoesFisicas", atleta.getLimitacoesFisicas());
        dadosAtleta.put("observacoes", atleta.getObservacoes());
        dadosAtleta.put("imc", calcularImc(atleta.getPeso(), atleta.getAltura()));

        Map<String, Object> dadosTreinador = new LinkedHashMap<>();
        dadosTreinador.put("id", atleta.getTreinador().getId());
        dadosTreinador.put("nome", atleta.getTreinador().getNome());

        Map<String, Object> dadosMetodologia = new LinkedHashMap<>();
        if (metodologia != null) {
            dadosMetodologia.put("titulo", metodologia.getTitulo());
            dadosMetodologia.put("descricao", metodologia.getDescricao());
            dadosMetodologia.put("estrategias", metodologia.getEstrategias());
            dadosMetodologia.put("recomendacoesAlimentares", metodologia.getRecomendacoesAlimentares());
            dadosMetodologia.put("criteriosEvolucao", metodologia.getCriteriosEvolucao());
        }

        Map<String, Object> dadosModalidade = new LinkedHashMap<>();
        if (atleta.getModalidade() != null) {
            dadosModalidade.put("id", atleta.getModalidade().getId());
            dadosModalidade.put("nome", atleta.getModalidade().getNome());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("atleta", dadosAtleta);
        payload.put("treinador", dadosTreinador);
        payload.put("metodologia", dadosMetodologia);
        payload.put("modalidade", dadosModalidade);

        return payload;
    }

    private double calcularImc(Double peso, Double altura) {
        if (peso == null || altura == null || altura == 0) return 0;
        return Math.round((peso / (altura * altura)) * 100.0) / 100.0;
    }

    private CronogramaResponseDto toDto(Cronograma c) {
        return CronogramaResponseDto.builder()
                .id(c.getId())
                .atletaId(c.getAtleta().getId())
                .atletaNome(c.getAtleta().getNome())
                .treinoSemanal(c.getTreinoSemanal())
                .dieta(c.getDieta())
                .observacoes(c.getObservacoes())
                .status(c.getStatus())
                .criadoEm(c.getCriadoEm())
                .build();
    }
}
