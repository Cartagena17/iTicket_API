package iTicket.Douglas.Modelos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModeloDTO {

    private Long idModelo;

    @NotBlank
    @Size(max = 20, message = "El nombre no puede superar los 20 caracteres")
    private String nombreModelo;

    @NotNull(message = "Debes indicar a que marca pertenece el modelo")
    private Long idMarca;
    private String nombreMarca;
}
