package iTicket.Douglas.Chatbot.DTO;

import jakarta.validation.constraints.NotBlank;
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
    // El usuario ya no viaja en el cuerpo: el backend lo resuelve de la cookie de sesion.

    @Positive(message = "El identificador de la conversación no es válido.")
    private Long idConversacion; // id de la conversacion

    @NotBlank(message = "Escribe un mensaje para continuar.") //Para que no se puedan enviar mensajes vacios
    @Size(
            max = 4000,
            message = "El mensaje no puede superar los 4000 caracteres."
    )
    private String mensaje; // id del mensaje para que se guarde en la conversación

}
