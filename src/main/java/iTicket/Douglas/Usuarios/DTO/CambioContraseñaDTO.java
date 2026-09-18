package iTicket.Douglas.Usuarios.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CambioContraseñaDTO {

    @NotBlank(message = "Debes indicar tu contraseña actual")
    private String claveActual;

    @NotBlank(message = "Debes indicar la nueva contraseña")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String claveNueva;
}
