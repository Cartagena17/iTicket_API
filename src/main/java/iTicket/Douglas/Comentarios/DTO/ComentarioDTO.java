package iTicket.Douglas.Comentarios.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComentarioDTO {

    private Long id;

    @NotBlank
    @Size(max = 300, message = "Longitud invalida en el comentario [300 caracteres como maximo]")
    private String comentario;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private  LocalDateTime fechaHora;

    @NotNull(message = "Debes indicar el ticket del comentario")
    private Long idTicket;

    @NotNull(message = "Debes indicar el usuario que creo el comentario")
    private Long idUsuarioComentario;
}
