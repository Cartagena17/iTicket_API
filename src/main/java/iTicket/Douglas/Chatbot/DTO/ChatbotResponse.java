package iTicket.Douglas.Chatbot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {
    // Esta es la respuesta que mostrara el chat
    private String respuesta;
    private Long idConversacion;
    private String titulo;
}
