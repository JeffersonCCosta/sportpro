package com.sportpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig — Configuração de segurança da aplicação.
 *
 * Responsabilidades:
 * 1. Define BCrypt como encoder de senha (fator de custo padrão = 10)
 * 2. Configura CORS para permitir chamadas do frontend
 * 3. Desabilita CSRF (APIs REST stateless não precisam)
 * 4. Libera todos os endpoints da API (JWT pode ser adicionado futuramente)
 *
 * Para adicionar JWT: injete JwtFilter antes de UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder — algoritmo de hash adaptativo.
     * Nunca armazene senhas em texto puro.
     * O hash inclui salt automático e é resistente a força bruta.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * FilterChain — define as regras de segurança HTTP.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF pois a API é stateless (sem sessão)
            .csrf(AbstractHttpConfigurer::disable)

            // Habilita CORS com as configurações definidas abaixo
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Libera todos os endpoints para desenvolvimento
            // TODO: proteger endpoints com JWT em produção
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * CORS — permite que o frontend (rodando em outra porta/domínio) acesse a API.
     * Em produção: substitua "*" pelos domínios específicos do frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens permitidas — em desenvolvimento: qualquer origem
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos nas requisições
        config.setAllowedHeaders(List.of("*"));

        // Permite envio de cookies e headers de autorização
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}
