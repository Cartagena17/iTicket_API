package iTicket.Douglas.Tickets.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketAsignacionDTO {

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @FutureOrPresent(message = "La fecha de vencimiento debe ser futura o actual")
    private LocalDate fechaVencimiento;

    @NotNull(message = "Es obligatorio asignar un técnico")
    @Positive(message = "ID de técnico inválido")
    private Long tecnicoAsignado;

    @NotNull(message = "La prioridad es obligatoria")
    private String prioridad;
}
