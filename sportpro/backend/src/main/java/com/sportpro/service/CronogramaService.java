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
 * Fluxo (RF008):
 * 1. Recebe atletaId
 * 2. Carrega atleta, treinador e PlanoTreino (modalidade + nível do atleta)
 * 3. Monta payload rico para o n8n
 * 4. Envia ao n8n via N8nWebhookIntegration
 * 5. Persiste o cronograma retornado
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CronogramaService {

    private final CronogramaRepository cronogramaRepository;
    private final AtletaRepository atletaRepository;
    private final PlanoTreinoRepository planoTreinoRepository;
    private final N8nWebhookIntegration n8nIntegration;

    @Transactional
    public CronogramaResponseDto gerarCronograma(Long atletaId) {
        log.info("Iniciando geração de cronograma para atleta ID: {}", atletaId);

        Atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado: ID " + atletaId));

        if (atleta.getTreinador() == null) {
            throw new ResourceNotFoundException("Atleta não possui treinador vinculado. Complete o perfil esportivo.");
        }

        if (atleta.getExperiencia() == null || atleta.getModalidadeNome() == null) {
            throw new ResourceNotFoundException("Complete o perfil esportivo com modalidade e nível de experiência.");
        }

        // Busca o PlanoTreino do treinador para a modalidade e nível do atleta
        Optional<PlanoTreino> planoOpt = planoTreinoRepository
                .findByTreinadorIdAndModalidadeAndNivel(
                        atleta.getTreinador().getId(),
                        atleta.getModalidadeNome(),
                        atleta.getExperiencia());

        PlanoTreino plano = planoOpt.orElse(null);

        if (plano == null) {
            log.warn("Treinador {} não possui plano para {} / {}. Usando fallback simulado.",
                    atleta.getTreinador().getNome(),
                    atleta.getModalidadeNome(),
                    atleta.getExperiencia());
        }

        Map<String, Object> payload = montarPayload(atleta, plano);
        Map<String, Object> resultado = n8nIntegration.enviarParaN8n(payload);

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

    private Map<String, Object> montarPayload(Atleta atleta, PlanoTreino plano) {

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

        Map<String, Object> dadosModalidade = new LinkedHashMap<>();
        if (atleta.getModalidadeNome() != null) {
            dadosModalidade.put("nome", atleta.getModalidadeNome());
        }

        Map<String, Object> dadosPlano = new LinkedHashMap<>();
        if (plano != null) {
            dadosPlano.put("descricaoGeral", plano.getDescricaoGeral());
            dadosPlano.put("estruturaSemana", plano.getEstruturaSemana());
            dadosPlano.put("tiposTreino", plano.getTiposTreino());
            dadosPlano.put("intensidades", plano.getIntensidades());
            dadosPlano.put("exerciciosForca", plano.getExerciciosForca());
            dadosPlano.put("recuperacao", plano.getRecuperacao());
            dadosPlano.put("metricasAvaliacao", plano.getMetricasAvaliacao());
            dadosPlano.put("cuidadosEspeciais", plano.getCuidadosEspeciais());
            dadosPlano.put("diasSemanaMin", plano.getDiasSemanaMin());
            dadosPlano.put("diasSemanaMax", plano.getDiasSemanaMax());
            dadosPlano.put("volumeSemanalKm", plano.getVolumeSemanalKm());
            dadosPlano.put("distribuicaoMacros", plano.getDistribuicaoMacros());
            dadosPlano.put("cafeDaManha", plano.getCafeDaManha());
            dadosPlano.put("almoco", plano.getAlmoco());
            dadosPlano.put("jantar", plano.getJantar());
            dadosPlano.put("preTreino", plano.getPreTreino());
            dadosPlano.put("posTreino", plano.getPosTreino());
            dadosPlano.put("duranteTreinoLongo", plano.getDuranteTreinoLongo());
            dadosPlano.put("hidratacao", plano.getHidratacao());
            dadosPlano.put("suplementacaoBase", plano.getSuplementacaoBase());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("atleta", dadosAtleta);
        payload.put("treinador", dadosTreinador);
        payload.put("modalidade", dadosModalidade);
        payload.put("planoTreino", dadosPlano);

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
