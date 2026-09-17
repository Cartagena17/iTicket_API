package iTicket.Douglas.Comentarios.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComentarioDTO {

    private Long id;

    @NotBlank
    @Size(max = 500, message = "Longitud invalida en el comentario [500 caracteres como maximo]")
    private String comentario;

    private LocalDateTime fechaHora;

    private Long idTicket;

    private Long idUsuarioComentario;

    //Solo de lectura: se completan al devolver el comentario, no se usan al crearlo/editarlo
    private String correoUsuario;

    private List<String> multimediaUrls;
}
