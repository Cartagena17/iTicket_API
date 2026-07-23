package iTicket.Douglas.DetalleTA.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleTADTO {

    private Long idDetalleTA;

    @NotNull(message = "Debe indicar el ticket relacionado")
    private Long idTicket;
    private String codigo;    // solo lectura

    @NotNull(message = "Debe indicar el artículo relacionado")
    private Long idArticulo;
    private String codigoArticulo;
}
