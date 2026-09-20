package iTicket.Douglas.Chatbot.Client;

import iTicket.Douglas.Chatbot.Config.GroqConfig;
import iTicket.Douglas.Chatbot.DTO.ChatMessageDTO;
import iTicket.Douglas.Chatbot.DTO.GroqResponse;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GroqClient {

    private final RestClient restClient; // Cliente HTTP que se usara para enviarle las solicitudes a Groq
    private final String model; // El modelo que esta usando Groq

    public GroqClient(GroqConfig config) {

        if (config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar GROQ_API_KEY en el servidor."
            );
        } // Aqui se valida si existe un valor o si esta vacia la api key

        if (config.getModel() == null || config.getModel().isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar GROQ_MODEL en el servidor."
            );
        } // Se conprueba lo mismo pero para el modelo

        this.model = config.getModel().trim();

        // Configura cuánto esperar para establecer la conexión, o sea dependiendo de esto puede que de la respuesta o se "recomienda" que la de
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // Configura cuánto esperar por la respuesta del proveedor, con esto se espera que no se espere indefinidamente la respuesta
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        this.restClient = RestClient.builder() // Aqui se une toda la configuración
                .baseUrl(config.getBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeaders(headers ->
                        headers.setBearerAuth(config.getApiKey().trim())
                )
                .build();
    }

    public String responder(
            String instrucciones,
            String contexto,
            List<ChatMessageDTO> historial,
            String mensaje
    ) { // Aqui generara la respuesta

        if (instrucciones == null || instrucciones.isBlank()) {
            throw new IllegalArgumentException(
                    "Faltan las instrucciones del asistente."
            );
        } // Evita que las instrucciones vengan vacias

        if (mensaje == null || mensaje.isBlank() || mensaje.length() > 4000) {
            throw new IllegalArgumentException(
                    "El mensaje debe contener entre 1 y 4000 caracteres."
            ); // Rechaza mensajes nulos
        }

        if (contexto == null || contexto.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta el contexto del asistente."
            );
        }

        List<Map<String, String>> mensajes = new ArrayList<>();
        mensajes.add(Map.of(
                "role", "system",
                "content", instrucciones
        ));
        mensajes.add(Map.of(
                "role", "system",
                "content", """
                        DATOS ACTUALES PROPORCIONADOS POR ITICKET:
                        %s

                        Estos datos actuales tienen prioridad sobre el historial.
                        Utilízalos únicamente para responder la pregunta.
                        No cambies sus valores ni inventes información adicional.
                        """.formatted(contexto)
        ));

        // El historial viene de la base de datos, nunca directamente del navegador.
        if (historial != null) {
            historial.stream()
                    .filter(anterior -> anterior != null
                            && ("user".equals(anterior.getRol())
                            || "assistant".equals(anterior.getRol()))
                            && anterior.getContenido() != null
                            && !anterior.getContenido().isBlank())
                    .forEach(anterior -> mensajes.add(Map.of(
                            "role", anterior.getRol(),
                            "content", anterior.getContenido()
                    )));
        }

        mensajes.add(Map.of(
                "role", "user",
                "content", mensaje
        ));

        // Aqui se construye el cuerpo de la solicitud
        // max_completion_tokens se bajo de 2048 a 1024: Ticky solo necesita
        // respuestas de 2 a 5 oraciones cortas, y un tope mas bajo evita que
        // una respuesta fuera de lo normal consuma de mas el limite de
        // tokens por minuto (TPM) del plan de Groq.
        Map<String, Object> solicitud = Map.of(
                "model", model,
                "messages", mensajes,
                "stream", false,
                "include_reasoning", false,
                "max_completion_tokens", 1024
        );

        try { // Envia la petición a Groq
            GroqResponse resultado = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(solicitud) // Aqui se establece lo que enviamos
                    .retrieve()
                    .body(GroqResponse.class); // Aqui se interptreta lo que recibimos

            if (resultado == null // Aqui se comprueba que hayan opciones de respuestas
                    || resultado.getChoices() == null
                    || resultado.getChoices().isEmpty()) {
                throw new IllegalStateException(
                        "El asistente no devolvió una respuesta."
                );
            }

            GroqResponse.Choice opcion = resultado.getChoices().get(0); //

            if (opcion == null) {
                throw new IllegalStateException(
                        "El asistente devolvió una respuesta inesperada."
                );
            } // Comprueba que la opción no sea nula

            // No presentamos un texto cortado como una respuesta completa
            if ("length".equals(opcion.getFinish_reason())) {
                throw new IllegalStateException(
                        "La respuesta quedó incompleta. Intenta una pregunta más concreta."
                );
            }

            if (!"stop".equals(opcion.getFinish_reason())
                    || opcion.getMessage() == null
                    || opcion.getMessage().getContent() == null
                    || opcion.getMessage().getContent().isBlank()) {
                throw new IllegalStateException(
                        "El asistente no pudo completar una respuesta de texto."
                );
            } // Se valida que termino correctamente la respuesta

            return opcion.getMessage().getContent().trim(); // Devuelve el texto final

        } catch (RestClientResponseException exception) { //
            int estado = exception.getStatusCode().value();

            // Se elige un mensaje dependiendo de lo que pase
            String detalle = switch (estado) {
                case 401, 403 -> // Autorizacion
                        "El proveedor rechazó el acceso del chatbot.";
                case 429 -> // Limite de solicitudes
                        "El asistente alcanzó su límite de uso. Inténtalo más tarde.";
                default ->
                        "No se pudo obtener una respuesta del asistente.";
            };

            throw new IllegalStateException(
                    detalle + " Código del proveedor: " + estado
            );

        } catch (ResourceAccessException exception) {
            throw new IllegalStateException(
                    "No se pudo conectar con el asistente o se agotó el tiempo de espera."
            );

        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "No se pudo interpretar la respuesta del asistente."
            );
        }
    }
}
