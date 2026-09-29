package iTicket.Douglas.Usuarios.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UsuarioUpdateDTO {

    @NotBlank @Size(max = 30, message = "El nombre no puede exceder los 30 caracteres")
    private String nombreUsuario;

    @NotBlank
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
    private String correo;

    // Sin campo de contraseña: al editar un usuario no se toca su clave

    @URL(message = "La URL de la imagen no es válida")
    @Size(max = 2050, message = "La URL no puede exceder los 2050 caracteres")
    private String imagenUrl;

    @NotNull(message = "Debe indicar el rol del usuario")
    private Long idRol;

    @NotNull(message = "Debe indicar el departamento del usuario")
    private Long idDepartamento;

    @NotNull(message = "Debe indicar el estado del usuario")
    private Boolean estado;
}
