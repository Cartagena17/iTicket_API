package iTicket.Douglas.Roles.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolDTO {
    private Long idRol;

    @NotBlank @Size(max = 20, message = "El nombre no puede exceder los 20 caracteres")
    private String nombreRol;
}
