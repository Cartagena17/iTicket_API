package iTicket.Douglas.Auth.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperacionRequestDTO {
    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "La nueva contraseAa es obligatoria")
    private String nuevaContrasena;
}