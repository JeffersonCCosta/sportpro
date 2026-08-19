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
 * Payload enviado ao n8n (limpo e direto):
 * {
 *   "atletaId":        2,
 *   "nome":            "Nome Atleta Teste",
 *   "idade":           31,
 *   "peso":            82.0,
 *   "altura":          1.82,
 *   "imc":             24.76,
 *   "modalidade":      "Meia-maratona",
 *   "nivel":           "INTERMEDIARIO",
 *   "diasDisponiveis": 5,
 *   "objetivo":        "...",
 *   "limitacoesFisicas": "...",
 *   "observacoes":     "...",
 *   "treinadorId":     4,
 *   "treinadorNome":   "Treinador 2 Teste"
 * }
 *
 * O n8n usa modalidade + nivel para buscar na base de conhecimento
 * do Supabase e gerar o cronograma via IA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CronogramaService {

    private final CronogramaRepository cronogramaRepository;
    private final AtletaRepository atletaRepository;
    private final N8nWebhookIntegration n8nIntegration;

    @Transactional
    public CronogramaResponseDto gerarCronograma(Long atletaId) {
        log.info("Iniciando geração de cronograma para atleta ID: {}", atletaId);

        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Atleta não encontrado: ID " + atletaId));

        if (atleta.getTreinador() == null) {
            throw new ResourceNotFoundException(
                    "Atleta não possui treinador vinculado. Complete o perfil esportivo.");
        }

        if (atleta.getExperiencia() == null || atleta.getModalidadeNome() == null) {
            throw new ResourceNotFoundException(
                    "Complete o perfil esportivo com modalidade e nível de experiência.");
        }

        // Monta payload limpo e envia ao n8n
        Map<String, Object> payload = montarPayload(atleta);
        Map<String, Object> resultado = n8nIntegration.enviarParaN8n(payload);

        // Persiste o cronograma retornado pelo n8n
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

    @Transactional(readOnly = true)
    public List<CronogramaResponseDto> listarPorAtleta(Long atletaId) {
        return cronogramaRepository.findByAtletaIdOrderByCriadoEmDesc(atletaId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Payload limpo enviado ao n8n.
     *
     * Apenas os dados do atleta necessários para:
     *  - Buscar na base de conhecimento: modalidade + nivel
     *  - Personalizar o cronograma: objetivo, limitações, dias disponíveis
     *
     * O planoTreino NÃO é enviado — o n8n busca direto do Supabase.
     */
    private Map<String, Object> montarPayload(Atleta atleta) {
        Map<String, Object> payload = new LinkedHashMap<>();

        // Identificação
        payload.put("atletaId",           atleta.getId());
        payload.put("nome",               atleta.getNome());

        // Dados físicos
        payload.put("idade",              atleta.getIdade());
        payload.put("peso",               atleta.getPeso());
        payload.put("altura",             atleta.getAltura());
        payload.put("imc",                calcularImc(atleta.getPeso(), atleta.getAltura()));

        // Chaves para busca na base de conhecimento do Supabase
        payload.put("modalidade",         atleta.getModalidadeNome());
        payload.put("nivel",              atleta.getExperiencia());
        payload.put("diasDisponiveis",    atleta.getDiasDisponiveis());

        // Personalização da IA
        payload.put("objetivo",           atleta.getObjetivo());
        payload.put("limitacoesFisicas",  atleta.getLimitacoesFisicas());
        payload.put("observacoes",        atleta.getObservacoes());

        // Treinador
        payload.put("treinadorId",        atleta.getTreinador().getId());
        payload.put("treinadorNome",      atleta.getTreinador().getNome());

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