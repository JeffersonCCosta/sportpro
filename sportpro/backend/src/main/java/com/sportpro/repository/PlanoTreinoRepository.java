package com.sportpro.repository;

import com.sportpro.entity.PlanoTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PlanoTreinoRepository — acesso a dados para PlanoTreino.
 */
@Repository
public interface PlanoTreinoRepository extends JpaRepository<PlanoTreino, Long> {

    /** Todos os planos de um treinador */
    List<PlanoTreino> findByTreinadorId(Long treinadorId);

    /** Busca plano específico por treinador + modalidade + nível */
    Optional<PlanoTreino> findByTreinadorIdAndModalidadeAndNivel(
            Long treinadorId, String modalidade, String nivel);

    /** Verifica se já existe plano para a combinação treinador+modalidade+nível */
    boolean existsByTreinadorIdAndModalidadeAndNivel(
            Long treinadorId, String modalidade, String nivel);
}
