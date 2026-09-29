package iTicket.Douglas.Auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginResponseDTO {

    private Long idUsuario;
    private String nombreUsuario;
    private String correo;
    private String nombreRol; // va directo en el claim "rol" del JWT
}
