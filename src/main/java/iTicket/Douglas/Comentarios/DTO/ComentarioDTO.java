package iTicket.Douglas.Comentarios.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComentarioDTO {

    private Long id;

    @NotBlank
    @Size(max = 300, message = "Longitud invalida en el comentario [300 caracteres como maximo]")
    private String comentario;

    private LocalDateTime fechaHora;

    private Long idTicket;

    private Long idUsuarioComentario;
}
