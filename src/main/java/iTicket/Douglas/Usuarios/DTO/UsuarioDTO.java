package iTicket.Douglas.Usuarios.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UsuarioDTO {

    private Long idUsuario;

    @NotBlank @Size(max = 20, message = "El nombre de usuario no debe sobrepasar los 20 caracteres" )
    String nombreUsuario;

    @NotBlank @Email(message = "El correo no tiene un formato válido")
    @Size(max = 30, message = "El correo no puede exceder los 30 caracteres")
    private String correo;

    @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clave;

    @URL(message = "La URL de la imagen no es válida")
    @Size(max = 300, message = "La URL no puede exceder los 300 caracteres")
    private String imagenUrl;

    @NotNull(message = "Debe indicar el rol del usuario")
    private Long idRol;
    private String nombreRol;

    @NotNull(message = "Debe indicar el departamento del usuario")
    private Long idDepartamento;
    private String nombreDepartamento;

    @NotNull(message = "Debe indicar el estado del usuario")
    private Boolean estado;
}
