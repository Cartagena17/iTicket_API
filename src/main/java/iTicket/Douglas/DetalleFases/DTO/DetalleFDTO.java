package iTicket.Douglas.DetalleFases.DTO;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DetalleFDTO {

    private Long idDetalleFase;
    @NotBlank @Size (max = 200, message = "La descripcion del detalle no puede exceder los 200 caracteres.")
    private String descripcionDetalle;


    private Boolean completado;

    @NotNull @Positive (message = "El id de la fase debe ser positivo.")
    private Long fase;
    private String nombreFase;
}
