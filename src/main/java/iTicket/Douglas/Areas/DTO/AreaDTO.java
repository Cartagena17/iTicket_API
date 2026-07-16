package iTicket.Douglas.Areas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AreaDTO {

    private Long idArea;
    @NotBlank @Size(max = 20, message = "El nombre no puede exceder los 20 caracteres")
    private String nombreArea;
}
