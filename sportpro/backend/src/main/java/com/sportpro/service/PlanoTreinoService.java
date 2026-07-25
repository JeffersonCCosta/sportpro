package com.sportpro.service;

import com.sportpro.dto.request.PlanoTreinoRequest;
import com.sportpro.dto.response.PlanoTreinoResponseDto;
import com.sportpro.entity.PlanoTreino;
import com.sportpro.entity.Treinador;
import com.sportpro.exception.BusinessException;
import com.sportpro.exception.ResourceNotFoundException;
import com.sportpro.repository.PlanoTreinoRepository;
import com.sportpro.repository.TreinadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PlanoTreinoService — Lógica de negócio para Planos de Treino.
 *
 * Regra principal: um treinador só pode ter UM plano por combinação
 * de modalidade + nível. Se tentar cadastrar novamente, faz UPDATE.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoTreinoService {

    private final PlanoTreinoRepository planoTreinoRepository;
    private final TreinadorRepository treinadorRepository;

    /**
     * Cadastra ou atualiza um plano de treino.
     * Se já existir plano para o treinador + modalidade + nível, faz UPDATE.
     */
    @Transactional
    public PlanoTreinoResponseDto salvar(PlanoTreinoRequest request) {
        Treinador treinador = treinadorRepository.findById(request.getTreinadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Treinador não encontrado"));

        // Verifica se já existe — se sim, atualiza ao invés de duplicar
        PlanoTreino plano = planoTreinoRepository
                .findByTreinadorIdAndModalidadeAndNivel(
                        request.getTreinadorId(),
                        request.getModalidade(),
                        request.getNivel())
                .orElse(new PlanoTreino());

        // Preenche todos os campos
        plano.setTreinador(treinador);
        plano.setModalidade(request.getModalidade());
        plano.setNivel(request.getNivel());

        // Treino
        plano.setDescricaoGeral(request.getDescricaoGeral());
        plano.setEstruturaSemana(request.getEstruturaSemana());
        plano.setTiposTreino(request.getTiposTreino());
        plano.setIntensidades(request.getIntensidades());
        plano.setExerciciosForca(request.getExerciciosForca());
        plano.setRecuperacao(request.getRecuperacao());
        plano.setMetricasAvaliacao(request.getMetricasAvaliacao());
        plano.setCuidadosEspeciais(request.getCuidadosEspeciais());
        plano.setDiasSemanaMin(request.getDiasSemanaMin());
        plano.setDiasSemanaMax(request.getDiasSemanaMax());
        plano.setVolumeSemanalKm(request.getVolumeSemanalKm());

        // Nutrição
        plano.setDistribuicaoMacros(request.getDistribuicaoMacros());
        plano.setCafeDaManha(request.getCafeDaManha());
        plano.setAlmoco(request.getAlmoco());
        plano.setJantar(request.getJantar());
        plano.setPreTreino(request.getPreTreino());
        plano.setPosTreino(request.getPosTreino());
        plano.setDuranteTreinoLongo(request.getDuranteTreinoLongo());
        plano.setHidratacao(request.getHidratacao());
        plano.setSuplementacaoBase(request.getSuplementacaoBase());

        PlanoTreino salvo = planoTreinoRepository.save(plano);
        log.info("Plano de treino salvo — ID: {}, Modalidade: {}, Nível: {}",
                salvo.getId(), salvo.getModalidade(), salvo.getNivel());

        return toDto(salvo);
    }

    /**
     * Lista todos os planos de um treinador.
     */
    @Transactional(readOnly = true)
    public List<PlanoTreinoResponseDto> listarPorTreinador(Long treinadorId) {
        return planoTreinoRepository.findByTreinadorId(treinadorId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Busca plano por ID.
     */
    @Transactional(readOnly = true)
    public PlanoTreinoResponseDto buscarPorId(Long id) {
        return toDto(planoTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado: ID " + id)));
    }

    /**
     * Busca plano por treinador + modalidade + nível.
     * Usado pelo CronogramaService para montar o payload do n8n.
     */
    @Transactional(readOnly = true)
    public PlanoTreinoResponseDto buscarPorModalidadeENivel(
            Long treinadorId, String modalidade, String nivel) {
        return toDto(planoTreinoRepository
                .findByTreinadorIdAndModalidadeAndNivel(treinadorId, modalidade, nivel)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano não encontrado para: " + modalidade + " / " + nivel)));
    }

    /**
     * Lista todos os planos (sem filtro de treinador).
     * Usado pelo frontend para montar a seleção de modalidade/treinador.
     */
    @Transactional(readOnly = true)
    public List<PlanoTreinoResponseDto> listarTodos() {
        return planoTreinoRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Exclui um plano de treino.
     */
    @Transactional
    public void excluir(Long id) {
        if (!planoTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plano não encontrado: ID " + id);
        }
        planoTreinoRepository.deleteById(id);
    }

    private PlanoTreinoResponseDto toDto(PlanoTreino p) {
        return PlanoTreinoResponseDto.builder()
                .id(p.getId())
                .treinadorId(p.getTreinador().getId())
                .treinadorNome(p.getTreinador().getNome())
                .modalidade(p.getModalidade())
                .nivel(p.getNivel())
                .descricaoGeral(p.getDescricaoGeral())
                .estruturaSemana(p.getEstruturaSemana())
                .tiposTreino(p.getTiposTreino())
                .intensidades(p.getIntensidades())
                .exerciciosForca(p.getExerciciosForca())
                .recuperacao(p.getRecuperacao())
                .metricasAvaliacao(p.getMetricasAvaliacao())
                .cuidadosEspeciais(p.getCuidadosEspeciais())
                .diasSemanaMin(p.getDiasSemanaMin())
                .diasSemanaMax(p.getDiasSemanaMax())
                .volumeSemanalKm(p.getVolumeSemanalKm())
                .distribuicaoMacros(p.getDistribuicaoMacros())
                .cafeDaManha(p.getCafeDaManha())
                .almoco(p.getAlmoco())
                .jantar(p.getJantar())
                .preTreino(p.getPreTreino())
                .posTreino(p.getPosTreino())
                .duranteTreinoLongo(p.getDuranteTreinoLongo())
                .hidratacao(p.getHidratacao())
                .suplementacaoBase(p.getSuplementacaoBase())
                .criadoEm(p.getCriadoEm())
                .atualizadoEm(p.getAtualizadoEm())
                .build();
    }
}
