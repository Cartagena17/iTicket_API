package iTicket.Douglas.Prioridades.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrioridadDTO {

    private Long idPrioridad;
    @NotBlank(message = "La prioridad debe tener un nombre")
    @Size(max = 20, message = "Longitud inválida en el nombre de la prioridad [20 caracteres]")
    private String nombrePrioridad;
}
