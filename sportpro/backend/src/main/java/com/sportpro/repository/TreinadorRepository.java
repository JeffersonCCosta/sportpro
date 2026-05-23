package com.sportpro.repository;

import com.sportpro.entity.Treinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * TreinadorRepository — Interface de acesso a dados para Treinador.
 *
 * Spring Data JPA gera automaticamente a implementação em tempo de execução.
 * Basta declarar métodos seguindo a convenção findBy<Campo>.
 */
@Repository
public interface TreinadorRepository extends JpaRepository<Treinador, Long> {

    /** Busca treinador pelo email — usado no login */
    Optional<Treinador> findByEmail(String email);

    /** Verifica se email já está em uso — evita duplicidade */
    boolean existsByEmail(String email);
}
