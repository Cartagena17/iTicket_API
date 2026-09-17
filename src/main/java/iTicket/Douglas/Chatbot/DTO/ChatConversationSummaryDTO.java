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
public class ChatConversationSummaryDTO {

    /**
     * Es el bloquesito de la conversación que se va guardando, el limite de conversaciones es 5
     * y si se pasa del límite, se elimina la más antigua
     * */

    private Long idConversacion;
    private String titulo;
    private LocalDateTime fechaActualizacion;
}
