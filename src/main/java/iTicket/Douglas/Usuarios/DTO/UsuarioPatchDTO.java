package iTicket.Douglas.Usuarios.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UsuarioPatchDTO {

    @Size(max = 30, message = "El nombre no puede exceder los 30 caracteres")
    private String nombreUsuario;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
    private String correo;

    // Sin campo de contraseña: al editar un usuario no se toca su clave

    @URL(message = "La url de la imagen no es válida")
    @Size(max = 2050, message = "La url  no puede exceder los 2050 caracteres")
    private String imagenUrl;

    private Long idRol;
    private Long idDepartamento;
}
