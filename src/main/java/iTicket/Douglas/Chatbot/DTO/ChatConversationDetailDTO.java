package iTicket.Douglas.Chatbot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversationDetailDTO {

    /**
     * Esta es toooda la conversacion desde el primer mensaje hasta el último.
     * */

    private Long idConversacion;
    private String titulo;
    private LocalDateTime fechaActualizacion;
    private List<ChatMessageDTO> mensajes;
}
