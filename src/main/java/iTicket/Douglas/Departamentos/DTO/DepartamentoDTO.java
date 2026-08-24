package iTicket.Douglas.Departamentos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartamentoDTO {

    private Long idDepartamento;

    @NotBlank
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombreDepartamento;

    //Se valida aqui para no depender del CHECK de Oracle y dar un mensaje claro
    @NotBlank(message = "Debes indicar el tipo de departamento")
    @Pattern(regexp = "IT|Mantenimiento|Otro",
             message = "El tipo de departamento debe ser 'IT', 'Mantenimiento' u 'Otro'")
    private String tipoDepartamento;

    @NotNull(message = "Debes indicar a que area pertenece el departamento")
    private Long idArea;
    private String nombreArea;
}
