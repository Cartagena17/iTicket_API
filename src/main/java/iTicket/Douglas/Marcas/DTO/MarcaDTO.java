package iTicket.Douglas.Marcas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarcaDTO {

    private Long idMarca;
    @NotBlank @Size(max = 20, message = "El nombre de la marca no puede exceder los 20 caracteres")
    private String nombreMarca;
}
