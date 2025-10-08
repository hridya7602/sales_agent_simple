package Salesllmproject.salesllmproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    private final WebClient webClient;
    private final String defaultModel;

    public LLMService(
            @Value("${groq.api.key:}") String apiKey,
            @Value("${groq.model:llama-3.1-8b-instant}") String defaultModel
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.defaultModel = defaultModel;
    }

    public String call(String prompt, Map<String, Object> options) {
        Object model = options != null ? options.getOrDefault("model", defaultModel) : defaultModel;
        Object temperature = options != null ? options.getOrDefault("temperature", 0.2) : 0.2;

        // Use standard chat completions endpoint
        Map<String, Object> chatRequest = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", temperature
        );

        try {
            Map response = webClient.post()
                    .uri("/chat/completions")  // ← CHANGED THIS
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(chatRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractChatContent(response);
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            System.err.println("Groq API Error: " + e.getStatusCode() + " - " + body);
            return "{\"error\":\"Groq API error: " + e.getStatusCode() + "\"}";
        } catch (Exception e) {
            System.err.println("LLM Service Error: " + e.getMessage());
            return "{\"error\":\"LLM error: " + e.getMessage() + "\"}";
        }
    }

    private String extractChatContent(Map response) {
        if (response == null) return "";
        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List)) return "";
        List choices = (List) choicesObj;
        if (choices.isEmpty()) return "";
        Object first = choices.get(0);
        if (!(first instanceof Map)) return "";
        Map firstChoice = (Map) first;
        Object messageObj = firstChoice.get("message");
        if (!(messageObj instanceof Map)) return "";
        Map message = (Map) messageObj;
        Object content = message.get("content");
        return content != null ? String.valueOf(content).trim() : "";
    }
}