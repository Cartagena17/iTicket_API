package iTicket.Douglas.DetalleTS.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DetalleTSDTO {

    private Long idDetalleTs;
    @NotBlank @Size (max = 50, message = "El nombre del software no puede exceder los 50 caracteres")
    private String nombreSoftware;
    @NotBlank
    @Size (max = 500, message = "La descripción de las ubicaciones no puede exceder los 500 caracteres")
    private String descripcionUbicaciones;
    @NotBlank @Size (max = 20, message = "La nombre de la versión no puede exceder los 20 caracteres")
    private String version;
    @NotNull
    @Positive(message = "El id del ticket es obligatorio")
    private Long ticket;
    private String asunto;
}
