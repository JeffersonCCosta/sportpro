package com.sportpro.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * N8nWebhookIntegration — Classe responsável pela comunicação com o n8n.
 *
 * ===== FLUXO DE INTEGRAÇÃO =====
 *
 *  Frontend                Backend               n8n Workflow
 *     │                      │                       │
 *     │── POST /perfil ──────▶│                       │
 *     │                      │── POST webhook ───────▶│
 *     │                      │                       │── (processamento)
 *     │                      │                       │── (IA opcional)
 *     │                      │◀─── JSON cronograma ──│
 *     │                      │── salva no banco      │
 *     │◀─── cronograma ──────│                       │
 *
 * ===== PAYLOAD ENVIADO AO N8N =====
 * {
 *   "atleta": { id, nome, idade, peso, altura, objetivo, experiencia, dias },
 *   "treinador": { id, nome, metodologia },
 *   "modalidade": { id, nome }
 * }
 *
 * ===== RETORNO ESPERADO DO N8N =====
 * {
 *   "treinoSemanal": "...",
 *   "dieta": "...",
 *   "observacoes": "..."
 * }
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class N8nWebhookIntegration {

    @Value("${n8n.webhook.url}")
    private String webhookUrl;

    private final ObjectMapper objectMapper;

    public Map<String, Object> enviarParaN8n(Map<String, Object> payload) {

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            log.info("========================================");
            log.info("INTEGRAÇÃO N8N");
            log.info("URL: {}", webhookUrl);
            log.info("Payload: {}", jsonPayload);
            log.info("========================================");

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(90))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            log.info("Enviando POST para o n8n...");

            long inicio = System.currentTimeMillis();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            long tempo = System.currentTimeMillis() - inicio;

            String body = response.body().trim();
            if (body.startsWith("=")) {
                body = body.substring(1).trim();
            }

            log.info("========================================");
            log.info("RESPOSTA DO N8N");
            log.info("Tempo: {} ms", tempo);
            log.info("Status: {}", response.statusCode());
            log.info("Body limpo: {}", body);
            log.info("========================================");

            if (response.statusCode() == 200 || response.statusCode() == 201) {

                @SuppressWarnings("unchecked")
                Map<String, Object> resultado =
                        objectMapper.readValue(body, Map.class); // ← aqui estava response.body()

                return resultado;
            }

            log.error(
                    "n8n retornou status inesperado: {}",
                    response.statusCode()
            );

            return gerarCronogramaSimulado(payload);

        } catch (Exception e) {

            log.error(
                    "ERRO NA COMUNICAÇÃO COM N8N: {}",
                    e.getMessage(),
                    e
            );

            return gerarCronogramaSimulado(payload);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> gerarCronogramaSimulado(
            Map<String, Object> payload) {

        log.info("Gerando cronograma simulado para desenvolvimento...");

        Map<String, Object> atleta =
                (Map<String, Object>) payload.get("atleta");

        String experiencia =
                atleta != null
                        ? String.valueOf(atleta.get("experiencia"))
                        : "INICIANTE";

        int dias =
                atleta != null
                        ? ((Number) atleta.getOrDefault(
                        "diasDisponiveis", 3)).intValue()
                        : 3;

        String treino = switch (experiencia.toUpperCase()) {
            case "AVANCADO" -> gerarTreinoAvancado(dias);
            case "INTERMEDIARIO" -> gerarTreinoIntermediario(dias);
            default -> gerarTreinoIniciante(dias);
        };

        return Map.of(
                "treinoSemanal", treino,
                "dieta", gerarDietaBase(),
                "observacoes",
                "Cronograma gerado automaticamente pelo sistema SportPro. " +
                        "Para personalização avançada com IA, configure a integração n8n."
        );
    }

    private String gerarTreinoIniciante(int dias) {
        return """
                === CRONOGRAMA SEMANAL — INICIANTE ===

                Segunda-feira: Corrida leve 20 min + alongamento 10 min
                Quarta-feira: Caminhada rápida 30 min + exercícios de mobilidade
                Sexta-feira: Corrida contínua 25 min + core básico (3x15 abdominais)

                Intensidade: Zona 2 (60-70% FCmax)
                Descanso: Priorize sono de qualidade (7-9h)
                """;
    }

    private String gerarTreinoIntermediario(int dias) {
        return """
                === CRONOGRAMA SEMANAL — INTERMEDIÁRIO ===

                Segunda: Treino intervalado 5x400m (75% esforço) + aquecimento 10 min
                Terça: Corrida regenerativa 30 min (pace confortável)
                Quarta: Treino técnico — passadas, cadência e postura
                Quinta: Descanso ativo (caminhada ou yoga)
                Sexta: Longão 45 min em pace aeróbico
                Sábado: Treino de força — agachamento, avanço, panturrilha (3x12)

                Intensidade: Zonas 2-4 conforme sessão
                Volume semanal: ~25-35 km
                """;
    }

    private String gerarTreinoAvancado(int dias) {
        return """
                === CRONOGRAMA SEMANAL — AVANÇADO ===

                Segunda: Tiro curto 10x200m (90% FCmax) + drills de sprint
                Terça: Regenerativo 40 min + mobilidade e ativação
                Quarta: Progressivo 8 km (últimos 2 km em pace de competição)
                Quinta: Força específica — terra, bom-dia, panturrilha pesada (4x8)
                Sexta: Intervalado longo 4x1000m (85% FCmax) com 3 min recuperação
                Sábado: Longão 60 min em pace moderado
                Domingo: Descanso completo

                Volume semanal: 50-70 km
                Monitorar: Variabilidade cardíaca (HRV) e lactato semanal
                """;
    }

    private String gerarDietaBase() {
        return """
                === PLANO ALIMENTAR BASE ===

                PRÉ-TREINO (1-2h antes):
                - Carboidratos de baixo índice glicêmico
                - Ex: aveia com banana + mel / batata-doce + frango
                - Hidratação: 500ml de água

                DURANTE TREINOS LONGOS (>60 min):
                - Gel energético ou fruta a cada 30-40 min
                - Água: 150-200ml a cada 15-20 min

                PÓS-TREINO (até 30 min após):
                - Proteína: 20-30g (whey ou ovo)
                - Carboidrato: batata-doce, arroz ou fruta
                - Ex: shake de whey + banana + pasta de amendoim

                REFEIÇÕES PRINCIPAIS:
                - Café da manhã: ovos, aveia, fruta
                - Almoço: arroz integral, feijão, proteína magra, salada
                - Jantar: proteína + legumes + carboidrato moderado

                HIDRATAÇÃO DIÁRIA: 35ml/kg de peso corporal
                SUPLEMENTAÇÃO: consulte seu treinador para individualização
                """;
    }
}