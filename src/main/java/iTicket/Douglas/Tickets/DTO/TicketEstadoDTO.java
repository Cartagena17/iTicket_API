package iTicket.Douglas.Tickets.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketEstadoDTO {

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 10, message = "Longitud inválida en el estado del ticket [10 caracteres]")
    private String estado;
}
