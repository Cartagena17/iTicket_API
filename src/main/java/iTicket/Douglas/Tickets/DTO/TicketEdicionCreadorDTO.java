package iTicket.Douglas.Tickets.DTO;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TicketEdicionCreadorDTO {

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 100, message = "Longitud inválida del asunto del ticket [100 caracteres]")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "Longitud inválida de la descripción del ticket [500 caracteres]")
    private String descripcion;

    @NotNull(message = "El departamento es obligatorio")
    @Positive
    private Long departamento;

    //Según tipoTicket
    private String descripcionUbicacion;
    private List<String> codigosArticulos;
    private List<DetalleTSDTO> detallesSoftware;
}
