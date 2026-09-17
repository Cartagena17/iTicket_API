package iTicket.Douglas.Chatbot.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatbotRequest {
    // Aqui se enviara el mensaje para preguntarle lo que sea al Chatbot

    @NotNull(message = "No se pudo identificar al usuario.")
    @Positive(message = "El identificador del usuario no es valido.") // Impide aceptar 0 o números negativos
    private Long idUsuario; // id del usuario que envio la petición para saber como le puede contestar

    @Positive(message = "El identificador de la conversación no es válido.")
    private Long idConversacion; // id de la conversacion

    @NotBlank(message = "Escribe un mensaje para continuar.") //Para que no se puedan enviar mensajes vacios
    @Size(
            max = 4000,
            message = "El mensaje no puede superar los 4000 caracteres."
    )
    private String mensaje; // id del mensaje para que se guarde en la conversación

}
