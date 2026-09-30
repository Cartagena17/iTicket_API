package iTicket.Douglas.Setup.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos del administrador inicial. El area y el departamento solo vienen cuando
 * la base esta completamente vacia
 */
@Data
public class SetupAdministradorDTO {

    @NotBlank(message = "Escribe tu nombre")
    @Size(max = 30, message = "El nombre de usuario no debe sobrepasar los 30 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "Escribe tu correo electronico")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
    private String correo;

    @NotBlank(message = "Escribe una contraseña")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String clave;

    private Long idDepartamento;

    private String nombreArea;
    private String nombreDepartamento;
    private String tipoDepartamento;
}
