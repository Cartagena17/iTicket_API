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

    @NotBlank
    @Size(max = 150, message = "Longitud invalida para el identificador de Cloudinary [150 caracteres]")
    private String cloudinaryId;

    private Long idComentario;
}
