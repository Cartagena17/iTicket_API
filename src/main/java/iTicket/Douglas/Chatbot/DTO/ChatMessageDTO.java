package iTicket.Douglas.Chatbot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    /**
     * Mensaje ya procesado para poder enviarse como una respuesta en el Chatbot de la
     * web o móvil.
     * */

    private Long idMensaje;
    private String rol;
    private String contenido;
    private LocalDateTime fechaHora;
}
