package com.sportpro.repository;

import com.sportpro.entity.Metodologia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MetodologiaRepository extends JpaRepository<Metodologia, Long> {
    List<Metodologia> findByTreinadorId(Long treinadorId);
}
