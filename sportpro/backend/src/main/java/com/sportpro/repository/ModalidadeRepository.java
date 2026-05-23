package com.sportpro.repository;

import com.sportpro.entity.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ModalidadeRepository — acesso a dados para Modalidade.
 */
@Repository
public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    /** Lista todas as modalidades de um treinador específico */
    List<Modalidade> findByTreinadorId(Long treinadorId);
}
