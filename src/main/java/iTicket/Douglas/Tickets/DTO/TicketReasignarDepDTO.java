package iTicket.Douglas.Tickets.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TicketReasignarDepDTO {

    @NotNull(message = "El departamento es obligatorio")
    @Positive
    private Long departamento;
}
