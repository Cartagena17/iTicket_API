package iTicket.Douglas.Tickets.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketAsignacionDTO {

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @FutureOrPresent(message = "La fecha de vencimiento debe ser futura o actual")
    private LocalDateTime fechaVencimiento;

    @NotNull(message = "Es obligatorio asignar un técnico")
    @Positive(message = "ID de técnico inválido")
    private Long tecnicoAsignado;

    @NotBlank(message = "La prioridad es obligatoria")
    @Size(max = 10, message = "Longitud inválida en la prioridad del ticket [10 caracteres]")
    private String prioridad;
}
