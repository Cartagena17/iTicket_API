package iTicket.Douglas.Areas.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AreaDTO {

    private Long idArea;
    @NotBlank @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombreArea;
}
