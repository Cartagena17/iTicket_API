package iTicket.Douglas.MultimediaComentario.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MultimediaComentarioDTO {

    private Long id;

    @NotBlank
    @Size(max = 255, message = "Longitud invalida para la URL [255 caracteres como maximo]")
    private String multimediaUrl;

    private Long idComentario;
}
