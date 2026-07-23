package iTicket.Douglas.Ubicaciones.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UbicacionDTO {

    private Long id;
    @NotBlank @Size(max = 50, message = "Longitud invalida en el nombre de la Ubicacion [50 caracteres como maximo]")
    private String nombreUbicacion;
    private Long idTipoUbicacion;
}
