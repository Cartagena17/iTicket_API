package iTicket.Douglas.Usuarios.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank @Email(message = "El correo no tiene un formato valido")
    private String correo;
    @NotBlank
    private String clave;
}
