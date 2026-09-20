package iTicket.Douglas.Chatbot.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GroqConfig {

    @Value("${groq.api-key}") // Llave de la api
    private String apiKey;

    @Value("${groq.model}") // Modelo de IA que se usara
    private String model;

    @Value("${groq.base-url}") // Base de la IA
    private String baseUrl;

}
