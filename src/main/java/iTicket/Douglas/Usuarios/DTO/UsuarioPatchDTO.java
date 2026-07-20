package iTicket.Douglas.Usuarios.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UsuarioPatchDTO {

    @Size(max = 20, message = "El nombre no puede exceder los 20 caracteres")
    private String nombreUsuario;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 30, message = "El correo no puede exceder los 20 caracteres")
    private String correo;

    @Size
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clave;

    @URL(message = "La url de la imagen no es válida")
    @Size(max = 300, message = "La url  no puede exceder los 300 caracteres")
    private String imagenUrl;

    private Long idRol;
    private Long idDepartamento;
}
