package iTicket.Douglas.Departamentos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartamentoDTO {

    private Long idDepartamento;

    @NotBlank
    @Size(max = 20, message = "El nombre no puede exceder los 20 caracteres")
    private String nombreDepartamento;

    @NotNull(message = "Debes indicar a que area pertenece el departamento")
    private Long idArea;
    private String nombreArea;
}
