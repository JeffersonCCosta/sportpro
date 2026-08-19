package com.sportpro.integration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TesteN8n {

    public static void main(String[] args) throws Exception {

        String url = "http://127.0.0.1:5678/webhook/sportpro";

        String json = """
                {
                    "teste": "JAVA",
                    "origem": "IntelliJ"
                }
                """;

        System.out.println("URL: " + url);
        System.out.println("Criando cliente...");

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        System.out.println("Enviando POST...");

        long inicio = System.currentTimeMillis();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        long fim = System.currentTimeMillis();

        System.out.println("Tempo: " + (fim - inicio) + " ms");
        System.out.println("STATUS: " + response.statusCode());
        System.out.println("BODY: " + response.body());
    }
}