package iTicket.Douglas.Tickets.DTO;

import iTicket.Douglas.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketResolucionDTO {

    @NotBlank(message = "Debe ingresar el diagnóstico o descripción de la falla")
    @Size (max = 500, message = "Longitud inválida de la descripción de la falla [500 caracteres]")
    private String descripcionFalla;

    @NotBlank(message = "La descripción de la solución es obligatoria.")
    @Size (max = 500, message = "Longitud inválida de la descripción de la solución [500 caracteres]")
    private String descripcionSolucion;
}
