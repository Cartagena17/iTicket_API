package iTicket.Douglas.TipoUbicacion.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TipoUbicacionDTO {

    private Long id;
    @NotBlank @Size(max = 50, message = "Longitud invalida en el nombre de Tipo Ubicacion [50 caracteres como maximo]")
    private String nombre_tipo_ubicacion;
}
