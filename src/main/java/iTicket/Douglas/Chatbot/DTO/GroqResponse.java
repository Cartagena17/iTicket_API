package iTicket.Douglas.Chatbot.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Con esto ignoramos completamente lo otro que devuelve la respuesta de Groq y solo agarramos lo que se necesita
public class GroqResponse {

    /**
     * Se necesita otro DTO de Response porque la respuesta que dara la api al cliente no es la misma que da groq a la request.
     * En la lista Choices tiene el porque se detuvo la respuesta (por ejemplo que ya finalizo) y el mensaje de respuesta que da
     * */

    private List<Choice> choices;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message; // Se trae el mensaje de respuesta de Groq
        private String finish_reason; // Y tambien la causa de porque se detuvo (por si hubo algun problema)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String content;
    }
}
