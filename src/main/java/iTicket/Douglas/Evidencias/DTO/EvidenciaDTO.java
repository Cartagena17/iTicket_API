package iTicket.Douglas.Evidencias.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EvidenciaDTO {

    private Long idEvidencia;
    @NotBlank(message = "La url de la evidencia es obligatoria")
    @Size(max = 2000, message = "Longitud inválida de la url de la imagen [2000 caracteres]")
    private String evidenciaUrl;

    @NotNull(message = "El id del ticket es obligatorio para guardar la evidencia")
    @Positive
    private Long ticket;
}

