package com.sportpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SportProApplication — Ponto de entrada da aplicação.
 *
 * @SpringBootApplication combina:
 *   - @Configuration: define beans no contexto
 *   - @EnableAutoConfiguration: configura automaticamente JPA, Web, Security etc.
 *   - @ComponentScan: varre o pacote com.sportpro em busca de componentes
 */
@SpringBootApplication
public class SportProApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportProApplication.class, args);
        System.out.println("""
                =============================================
                  SportPro Backend iniciado com sucesso!
                  API disponível em: http://localhost:8080
                =============================================
                """);
    }
}
