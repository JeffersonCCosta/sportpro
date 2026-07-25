package com.sportpro.repository;

import com.sportpro.entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * AtletaRepository — acesso a dados para Atleta.
 */
@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {
    Optional<Atleta> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Atleta> findByTreinadorId(Long treinadorId);
    List<Atleta> findByModalidadeNome(String modalidadeNome);
}
