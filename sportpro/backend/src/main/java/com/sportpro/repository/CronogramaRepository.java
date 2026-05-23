package com.sportpro.repository;

import com.sportpro.entity.Cronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma, Long> {
    /** Histórico de cronogramas de um atleta, ordenado do mais recente */
    List<Cronograma> findByAtletaIdOrderByCriadoEmDesc(Long atletaId);
}
