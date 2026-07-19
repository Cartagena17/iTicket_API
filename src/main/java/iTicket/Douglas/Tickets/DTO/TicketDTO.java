package iTicket.Douglas.Tickets.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketDTO {

    private Long idTicket;
    @Size(max = 15, message = "Longitud inválida del código del ticket [15 caracteres]")
    private String codigo; //Se genera automáticamente

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 50, message = "Longitud inválida del asunto del ticket [50 caracteres]")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "Longitud inválida de la descripción del ticket [100 caracteres]")
    private String descripcion;

    @NotNull(message = "El departamento es obligatorio")
    @Positive
    private Long departamento;
    private String nombreDepartamento;//Este atributo solo se mostrará en los GET

    @Size(max = 100, message = "Longitud inválida de la descripción de la falla [100 caracteres]")
    private String descripcionFalla;

    @Size (max = 100, message = "Longitud inválida de la descripción de la solución [100 caracteres]")
    private String descripcionSolucion;

    @FutureOrPresent(message = "La fecha de vencimiento debe ser futura o actual")
    private LocalDate fechaVencimiento;

    @Positive
    private Long tecnicoAsignado;
    private String nombreTecnico;//Este atributo solo se mostrará en los GET

    @Positive
    private Long prioridad;
    private String nombrePrioridad;//Este atributo solo se mostrará en los GET
}
